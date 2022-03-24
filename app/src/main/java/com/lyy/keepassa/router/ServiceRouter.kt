/*
 * Copyright (C) 2020 AriaLyy(https://github.com/AriaLyy/KeepassA)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.lyy.keepassa.router

import com.arialyy.frame.router.RouterPath
import com.lyy.keepassa.service.feat.KdbSaveService

/**
 * @Author laoyuyu
 * @Description
 * @Date 2:05 下午 2022/3/24
 **/
internal interface ServiceRouter {

  @RouterPath(path = "/service/dbSave")
  fun getDbSaveService(): KdbSaveService
}