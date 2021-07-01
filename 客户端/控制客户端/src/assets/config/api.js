const RootUrl = 'http://stu-project-test.qingxu.website:10008';
// const RootUrl = 'http://localhost:10008';
const Account = '/ACCOUNT-PROVIDER';
const Indoor = '/INDOOR-PROVIDER';
const Guardian = '/GUARDIAN-SERVICE';
const Outdoor="/PROTECT-PROVIDER";
const Audio='/AUDIO-SERVICE';
const Version = '/v0.0.1'
const codeToStr = (code) => {
  switch (code) {
    case 0:
      return "正常"
    case 1:
      return "错误"
    case 2:
      return "警告"
    case 3:
      return "token错误"
    case 4:
      return "等待中"
    default:
      return
  }
}
module.exports = {
  //UtilFunctions
  codeToStr: codeToStr,
  SUCCESS: 0,
  ERROR: 1,
  WARNING: 2,
  TOKEN_ISSUE: 3,
  WAITING: 4,
  //ACCOUNT-PROVIDER
  register: RootUrl + Account + Version + '/account/register',
  login: RootUrl + Account + Version + '/account/login',
  refreshShareCode: RootUrl + Account + Version + '/account/refreshShareCode',
  applyShareCode: RootUrl + Account + Version + '/account/applyShareCode',
  removeGuard: RootUrl + Account + Version + '/account/removeGuard',
  applyShareCode:RootUrl + Account + Version + '/account/applyShareCode',
  refreshShareCode:RootUrl + Account + Version + '/account/refreshShareCode',
  changeNickname:RootUrl + Account + Version + '/account/changeNickname',
  changeLoginInfo:RootUrl + Account + Version + '/account/changeLoginInfo',
  //INDOOR-PROVIDER
  getMyCameraList: RootUrl + Indoor + Version + '/camera/my-list',
  getWardCameraList: RootUrl + Indoor + Version + '/camera/ward-list',
  getCameraList: RootUrl + Indoor + Version + '/camera/list',
  launchStream: RootUrl + Indoor + Version + '/stream/launch-stream',
  streamHeartBeat: RootUrl + Indoor + Version + '/stream/stream-heartbeat',
  //AUDIO-SERVICE
  getAudioFileInfos:RootUrl+Audio+'/file/infos/audio',
  //GUARDIAN-SERVICE
  getWarningRecords:RootUrl+Guardian+'/guardian/list/warning',
  getWardList: RootUrl + Guardian + '/guardian/list/ward',
  //OUTDOOR-SERVICE
  getTrackRecently: 'http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getAllTrackRecently',

  getUserInfo:RootUrl + Guardian +'/guardian/userinfo'
}