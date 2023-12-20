/*
 * Copyright (C) 2020 AriaLyy(https://github.com/AriaLyy/KeepassA)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */


package com.lyy.keepassa.view.create

import KDBAutoFillRepository
import android.content.Context
import android.graphics.Bitmap.CompressFormat.PNG
import android.net.Uri
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.arialyy.frame.util.ResUtil
import com.keepassdroid.database.PwDatabaseV4
import com.keepassdroid.database.PwEntry
import com.keepassdroid.database.PwEntryV4
import com.keepassdroid.database.PwGroupV4
import com.keepassdroid.database.PwIconCustom
import com.keepassdroid.database.PwIconStandard
import com.keepassdroid.database.security.ProtectedBinary
import com.keepassdroid.database.security.ProtectedString
import com.keepassdroid.utils.UriUtil
import com.lyy.keepassa.R
import com.lyy.keepassa.base.BaseApp
import com.lyy.keepassa.base.BaseModule
import com.lyy.keepassa.entity.AutoFillParam
import com.lyy.keepassa.entity.SimpleItemEntity
import com.lyy.keepassa.entity.TagBean
import com.lyy.keepassa.util.HitUtil
import com.lyy.keepassa.util.IconUtil
import com.lyy.keepassa.util.KdbUtil
import com.lyy.keepassa.util.KpaUtil
import com.lyy.keepassa.util.getFileInfo
import com.lyy.keepassa.util.hasNote
import com.lyy.keepassa.util.hasTOTP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.UUID

/**
 * 创建条目、群组的module
 */
class CreateEntryModule : BaseModule() {
  companion object {
    val attrFlow = MutableStateFlow<Pair<String, ProtectedBinary>?>(null)
    val userNameFlow = MutableStateFlow<List<String>?>(null)
  }

  /**
   * 已经选中的标签
   */
  var selectedTagBeanCache = mutableListOf<String>()
  var customIcon: PwIconCustom? = null
  val attrStrMap = LinkedHashMap<String, ProtectedString>()
  val attrFileMap = LinkedHashMap<String, ProtectedBinary>()
  var icon = PwIconStandard(0)
  var loseDate: Date? = null // 失效时间
  var userNameCache = arrayListOf<String>()
  var noteStr: CharSequence = ""
  var expires: Boolean = false
  var autoFillParam: AutoFillParam? = null
  lateinit var pwEntry: PwEntryV4

  fun cacheTag(tagList: List<TagBean>) {
    selectedTagBeanCache.clear()
    selectedTagBeanCache.addAll(tagList.filter { it.isSet }.map {
      it.tag
    })
  }

  /**
   * 添加附件
   */
  fun addAttrFile(context: CreateEntryActivity, uri: Uri?) {
    val rootView = context.rootView
    if (uri == null) {
      Timber.e("附件uri为空")
      HitUtil.snackShort(
        rootView,
        "${ResUtil.getString(R.string.add_attr_file)}${ResUtil.getString(R.string.fail)}"
      )
      return
    }
    val fileInfo = uri.getFileInfo(context)
    if (TextUtils.isEmpty(fileInfo.first) || fileInfo.second == null) {
      Timber.e("获取文件名失败")
      HitUtil.snackShort(
        rootView,
        "${ResUtil.getString(R.string.add_attr_file)}${ResUtil.getString(R.string.fail)}"
      )
      return
    }
    val fileName = fileInfo.first!!
    val fileSize = fileInfo.second!!
    if (fileSize >= 1024 * 1024 * 10) {
      HitUtil.snackShort(rootView, ResUtil.getString(R.string.error_attr_file_too_large))
      return
    }
    context.lifecycleScope.launch {
      attrFlow.emit(
        Pair(
          fileName, ProtectedBinary(
            false, UriUtil.getUriInputStream(context, uri)
              .readBytes()
          )
        )
      )
    }
  }

  fun isFormAutoFill() = autoFillParam != null

  /**
   * Traverse database and get all userName
   */
  fun getUserNameCache() {
    if (userNameCache.isNotEmpty()) {
      viewModelScope.launch {
        userNameFlow.emit(userNameCache)
      }
      return
    }
    val temp = hashSetOf<String>()
    for (map in BaseApp.KDB.pm.entries) {
      if (map.value.username.isNullOrEmpty()) {
        continue
      }
      val userName = KdbUtil.getUserName(map.value)
      temp.add(userName)
    }
    userNameCache.addAll(temp)
    viewModelScope.launch {
      userNameFlow.emit(userNameCache)
    }
  }

  /**
   * 更新实体
   */
  fun updateEntry(
    entry: PwEntryV4,
    title: String,
    userName: String?,
    pass: String?,
    url: String,
    tags: String
  ) {
    if (customIcon != null) {
      entry.customIcon = customIcon
    }
    entry.tags = tags
    if (attrStrMap.isNotEmpty()) {
      entry.strings.clear()
      entry.strings.putAll(attrStrMap)
    } else {
      entry.strings.clear()
    }
    if (attrFileMap.isNotEmpty()) {
      val binPool = (BaseApp.KDB.pm as PwDatabaseV4).binPool
      entry.binaries.clear()
      for (d in attrFileMap) {
        entry.binaries[d.key] = d.value
        if (binPool.poolFind(d.value) == -1) {
          binPool.poolAdd(d.value)
        }
      }
    } else {
      entry.binaries.clear()
    }

    entry.setTitle(title, BaseApp.KDB.pm)
    entry.setUsername(userName, BaseApp.KDB.pm)
    entry.setPassword(pass, BaseApp.KDB.pm)
    entry.setUrl(url, BaseApp.KDB.pm)

    if (noteStr.isNotEmpty()) {
      Timber.d("notes = $noteStr")
      entry.setNotes(noteStr.toString(), BaseApp.KDB.pm)
    }
    entry.setExpires(expires)
    if (loseDate != null) {
      entry.expiryTime = loseDate
    }
    entry.icon = icon
  }

  /**
   * 是否已经存在totp
   * @return false 不存在
   */
  fun hasTotp(): Boolean {
    pwEntry.strings.forEach {
      if (it.value.isOtpPass) {
        return true
      }
    }
    return false
  }

  /**
   * 自动填充进行保存数据时，搜索条目信息，如果条目不存在，新建条目
   */
  fun getEntryFromAutoFillSave(
    context: Context,
    apkPkgName: String,
    userName: String?,
    pass: String?
  ): PwEntryV4 {
    val listStorage = ArrayList<PwEntry>()
    KdbUtil.searchEntriesByPackageName(apkPkgName, listStorage)
    val entry: PwEntryV4
    if (listStorage.isEmpty()) {
      entry = PwEntryV4(BaseApp.KDB.pm.rootGroup as PwGroupV4)
      val icon = IconUtil.getAppIcon(context, apkPkgName)
      if (icon != null) {
        val baos = ByteArrayOutputStream()
        icon.compress(PNG, 100, baos)
        val datas: ByteArray = baos.toByteArray()
        val customIcon = PwIconCustom(UUID.randomUUID(), datas)
        entry.customIcon = customIcon
        (BaseApp.KDB.pm as PwDatabaseV4).putCustomIcons(customIcon)
        entry.strings["KP2A_URL_1"] = ProtectedString(false, "androidapp://$apkPkgName")
      }

      val appName = KDBAutoFillRepository.getAppName(context, apkPkgName)
      entry.setTitle(appName ?: "newEntry", BaseApp.KDB.pm)
      entry.icon = PwIconStandard(0)
    } else {
      entry = listStorage[0] as PwEntryV4
      Timber.w("已存在含有【$apkPkgName】的条目，将更新条目")
    }
    if (!userName.isNullOrEmpty()) {
      entry.setUsername(userName, BaseApp.KDB.pm)
    }
    if (!pass.isNullOrEmpty()) {
      entry.setPassword(pass, BaseApp.KDB.pm)
    }
    return entry
  }

  /**
   * 创建群组
   * @param groupName 群组名
   * @param parentGroup 父群组
   * @param icon 标准图标
   * @param customIcon 自定义图标
   */
  fun createGroup(
    groupName: String,
    parentGroup: PwGroupV4,
    icon: PwIconStandard,
    customIcon: PwIconCustom?,
    callback: (PwGroupV4) -> Unit
  ) {
    KpaUtil.kdbHandlerService.createGroup(groupName, icon, customIcon, parentGroup, callback)
  }

  /**
   * 添加条目
   * @param pwEntry 需要添加的条目
   */
  fun addEntry(ac: FragmentActivity, pwEntry: PwEntryV4) {
    KpaUtil.kdbHandlerService.addEntry(pwEntry)
    KpaUtil.kdbHandlerService.saveDbByForeground {
      HitUtil.toaskShort(
        "${BaseApp.APP.getString(R.string.create_entry)}${
          BaseApp.APP.getString(
            R.string.success
          )
        }"
      )
      ac.finishAfterTransition()
    }
  }

  /**
   * 保存条目
   */
  fun saveDb(callback: (Int) -> Unit) {
    viewModelScope.launch {
      KpaUtil.kdbHandlerService.saveOnly(true, callback)
    }
  }

  /**
   * 构建的更多选择项目
   */
  fun getMoreItem(context: Context): ArrayList<SimpleItemEntity> {
    val list = ArrayList<SimpleItemEntity>()
    val titles = context.resources.getStringArray(R.array.v4_add_mor_item)
    val icons = context.resources.obtainTypedArray(R.array.v4_add_more_icon)
    val len = titles.size - 1
    for (i in 0..len) {
      val item = SimpleItemEntity()
      item.title = titles[i]
      item.icon = icons.getResourceId(i, 0)
      if (item.icon == R.drawable.ic_token_grey && pwEntry.hasTOTP()) {
        Timber.d("Already used totp")
        continue
      }
      if (item.icon == R.drawable.ic_notice && pwEntry.hasNote()) {
        Timber.d("Already used note")
        continue
      }
      list.add(item)
    }
    icons.recycle()
    return list
  }
}