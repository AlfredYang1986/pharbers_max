package module.common.em

import play.api.libs.json.JsValue
import module.common.em.EmMessage._
import module.common.MergeStepResult
import com.pharbers.bmpattern.ModuleTrait
import com.pharbers.bmmessages.{CommonModules, MessageDefines}

object EmModule extends ModuleTrait with EmTrait {

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])
                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        // user manager
        case msg_registerUserForEm(jv) => registerUser(jv)(MergeStepResult(jv, pr))
        case msg_deleteUserForEm(jv) => deleteUser(MergeStepResult(jv, pr))
        case msg_disconnectUserForEm(jv) => disconnectUser(MergeStepResult(jv, pr))
        case msg_modifyPosswordForEm(jv) => modifyPossword(MergeStepResult(jv, pr))

        // chatgroup manager
        case msg_queryAllChatgroupForEm(jv) => queryAllChatgroup(MergeStepResult(jv, pr))
        case msg_queryGroupIdByNameForEm(jv) => queryGroupIdByName(MergeStepResult(jv, pr))
        case msg_createChatgroupForEm(jv) => createChatgroup(jv)(MergeStepResult(jv, pr))
        case msg_deleteChatgroupForEm(jv) => deleteChatgroup(MergeStepResult(jv, pr))

        // user and chatgroup relation
        case msg_userJoinChatgroupForEm(jv) => userJoinChatgroup(MergeStepResult(jv, pr))
        case msg_userQuitChatgroupForEm(jv) => userQuitChatgroup(MergeStepResult(jv, pr))

        case _ => ???
    }

}
