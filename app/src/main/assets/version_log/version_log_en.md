#### 2.0.1（2021/3/27）
- [Fix]Webdav login problem
- [Fix]Multilingual issues

#### 2.0（2021/3/23）
- [New]The recycle bin does not display the add group\entry button
- [New]Whether to hide the status bar，[Setting](route://keepassA.com/kpa?activity=SettingActivity) -> Ui Setting
- [New]Key login only
- [Optimize]Lots of animations
- [Optimize]The default icons are all MD style icons
- [Optimize]Icon selection will use the method that pops up at the bottom
- [Fix]The problem of invalid timing lock database
- [Fix]The problem of missing jump animation
- [Fix]Unable to switch historical data

#### 1.8.4 （2021/3/4）
- Refactor the logic of creating a database
- Increase the function of creating a database on Webdav

![webdavCreate](https://raw.githubusercontent.com/AriaLyy/KeepassA/master/img/webdavCreate.png)
- Fix some crashes

#### 1.8.3 (2021/2/25)
- Fix some crashes
- Upgrade kotlin version
- Remove kotlin-android-extensions

#### 1.8.2 (2021/2/17)
- Fix the crash of the software after the screen is locked
- Fix the crash of getting steam totp
- Upgrade kotlin version

#### 1.8.1 (2021/2/14)
- Fix database loading due to incomplete reading
- Fix asserts
- Add Argon2 ID support
- Add keyfile 2.0 support

#### 1.8 (2021/2/14)
- Fix a problem that webdav cannot be opened due to cache
- Optimize the speed of opening the database

#### 1.7 (2021/2/4)
- Add, When the package name does not match, the keyword will be matched from the url
- Add, Reference entry hint
- Add, [When screen lock, automatic lock database function](route://keepassA.com/kpa?activity=SettingActivity&type=app)
- Add, note add editer
- Add, User name history record function

![user_drop_down_list](https://raw.githubusercontent.com/AriaLyy/KeepassA/master/img/userDropdownList.png)
- Add, Search ignores items in the recycle bin
- Fix, In night mode, auto-fill text cannot be displayed
- Fix, Some applications cannot be automatically filled
- Fix, Some crash issues

#### 1.6 (2020/11/28)
- Auto-fill module, after fixing the associated entry, return to the application, the associated data cannot be displayed
- Auto-fill module, fix the problem that the input box data is filled incorrectly when multiple input boxes are filled
- Auto-fill module, add browser filling function
- Auto-fill module, add other buttons
- Setup module, After the unlock database, the homepage will display all entries first, [Click Settings](route://keepassA.com/kpa?activity=SettingActivity&type=db)
- Item details module，Note field automatically increase expansion and contraction functions

![ime](https://raw.githubusercontent.com/AriaLyy/KeepassA/master/img/noteExpand.png)
- Optimize the homepage flickering problem
- Thank [@DominicDesbiens](https://github.com/DominicDesbiens) for providing the Canadian French translation

#### 1.5 (2020/11/5)
- Add group search function
- Add open source protocol description
- Increase the function of moving items and groups

![ime](https://raw.githubusercontent.com/AriaLyy/KeepassA/master/img/moveData.png)
- Fix the problem that the secondary group cannot be added
- Fix a crash caused by webdav obtaining file information
- Fix some nasty crashes

#### 1.4.1 (2020/10/29)
- Upgrade android studio to 4.1
- Search and add highlight keywords
- Fix a crash problem of quick unlock

#### 1.4 (2020/10/28)
- Upgrade kotlin version
- Add group sorting function
- Add field reference function
- Add [safe keyboard](route://keepassA.com/kpa?activity=ime)

![ime](https://raw.githubusercontent.com/AriaLyy/KeepassA/master/img/ime.png)
- Fix Quickly unlock the interface, the problem that all short passwords cannot be deleted
- Fix the crash problem caused by webdav login timeout
- Fix some annoying crash problems

#### 1.3 (2020/9/22)
- Add the TOTP token setting function, edit the entry, click the add more button to display the function interface

![otp_setting](https://raw.githubusercontent.com/AriaLyy/KeepassA/master/img/otpsetting.png)

#### 1.2 (2020/9/2)
- Fix some nasty crashes

#### 1.1 (2020/8/21)
- KeepassA is open source and has been hosted on [github](https://github.com/AriaLyy/KeepassA)
- Fixed some annoying bugs

#### 1.0.1.2 (2020/7/22)
- Refactored [Fingerprint Unlock Setting Interface](route://keepassA.com/kpa?activity=FingerprintActivity)
- Add drop-down synchronization database function
- Add mobile phone root/emulator detection
- Add the function of directly deleting entries/groups
- Fix the problem that http addresses with special characters cannot be recognized
- Fix the problem that the auto-fill service cannot save data

#### 1.0.1.0 (2020/7/10)
- Fixed the issue of clicking the notification bar to notify the error when the database is locked
- TOTP increase countdown effect
- Fixed the problem that the same entry always prompts when synchronizing the database
- Fix some nasty crashes

#### 1.0.0.9 (2020/7/11)
- Fix the problem that after quick lock manually, you cannot enter other pages
- Fix the problem that the notification bar icon is too small
- Fixed the crash problem when opening the fingerprint unlock interface
- Fixed the crash of auto-fill service
- Optimize the logic of fingerprint unlock function

#### 1.0.0.7 (2020/6/14)
- Add history version dialog
- Increase vibration feedback
- Fix the problem that the dropbox open record cannot be saved

#### 1.0.0.6 (2020/6/7)
- Fix some nasty bugs
- add [Fingerprint unlock](route://keepassA.com/kpa?activity=FingerprintActivity)

#### 1.0.0.4 (2020/6/6)
- Fix some nasty bugs

#### 1.0.0.3 (2020/5/13)
- Fix some nasty bugs

#### 1.0.0.2 (2020/5/10)
- Fix some nasty bugs

#### 1.0.0.1 (2020/5/3)
- Fix some nasty bugs
