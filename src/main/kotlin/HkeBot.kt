package me.dousha

import br.com.azalim.mcserverping.MCPing
import kotlinx.serialization.Serializable
import me.dousha.PluginConfig.banList
import me.dousha.PluginConfig.joinGrouMessage
import me.dousha.PluginConfig.mcServerInfo
import me.dousha.PluginConfig.motdMessage
import me.dousha.PluginConfig.muteTexts
import me.dousha.PluginConfig.muteTime
import me.dousha.PluginConfig.recallTexts
import me.dousha.PluginConfig.replyMessageAccurate
import me.dousha.PluginConfig.replyMessageFuzzy
import me.dousha.PluginData.playerInfoAlready
import me.dousha.PluginData.playerInfoUnready
import me.dousha.PluginData.rconServerInfo
import me.dousha.PluginSignData.signList
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.console.util.ContactUtils.getContactOrNull
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import net.mamoe.mirai.utils.info
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.regex.Pattern


object HkeBot : KotlinPlugin(JvmPluginDescription(
    id = "me.dousha.hkebot",
    name = "HkeBot",
    version = "0.1.0",
) {
    author("dousha")
}) {
    private val rconServer: Rcon? = null
    override fun onEnable() {
        PluginData.reload()
        PluginSignData.reload()
        PluginConfig.reload()
        PluginCompositeCommand.register()
        openRcon(rconServerInfo.ip, rconServerInfo.port, rconServerInfo.password)
        logger.info {
            "\n" + " __    __   __  ___  _______ .______     ______   .___________.\n" + "|  |  |  | |  |/  / |   ____||   _  \\   /  __  \\  |           |\n" + "|  |__|  | |  '  /  |  |__   |  |_)  | |  |  |  | `---|  |----`\n" + "|   __   | |    <   |   __|  |   _  <  |  |  |  |     |  |     \n" + "|  |  |  | |  .  \\  |  |____ |  |_)  | |  `--'  |     |  |     \n" + "|__|  |__| |__|\\__\\ |_______||______/   \\______/      |__|     \n" + "                                                               \n"
        }
        onJoinGroup()
        onReply()
        onMuteEvent()
    }

    override fun onDisable() {
        rconServer!!.close()
    }

    private fun openRcon(ip: String, port: Int, password: String): Boolean {
        kotlin.runCatching {
            Rcon.open(ip, port)
        }.onSuccess { rcon ->
            if (!rcon.authenticate(password)) {
                logger.error("验证失败,请检查密码是否正确")
                rcon.close()
            } else {
                return true
            }
        }.onFailure {
            logger.error(it)
        }
        return false
    }

    fun sendCommand(command: String): String {
        return try {
            rconServer!!.sendCommand(command)
        } catch (e: IOException) {
            "服务器连接失败!!!"
        }
    }

    @OptIn(ConsoleExperimentalApi::class)
    fun onJoinGroup() {
        GlobalEventChannel.subscribeAlways<MemberJoinEvent> {
            member.sendMessage(joinGrouMessage.replace("{group}", member.group.name))
        }
        GlobalEventChannel.subscribeAlways<MemberJoinRequestEvent> {
            val user = bot.getContactOrNull(fromId) as User
            if (banList.contains(fromId)) {
                reject()
                return@subscribeAlways
            }
            //大于15级别的QQ可以加入群
            val qLevel = user.queryProfile().qLevel
            if (qLevel >= 15) {
                accept()
            }
        }
    }

    @OptIn(ConsoleExperimentalApi::class)
    private fun onMuteEvent() {
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            val msg = it.message.contentToString()
            muteTexts.forEach { fuck ->
                if (msg.contains(fuck)) {
                    sender.mute(muteTime)
                    group.sendMessage("${sender.nameCardOrNick}已被禁言${muteTime}秒")
                }
            }
            recallTexts.forEach { fuck ->
                if (msg.contains(fuck)) {
                    message.recall()
                    group.sendMessage("已撤回${sender.nameCardOrNick}的消息")
                }
            }
            // TODO: 管理员改名字
            val at = message.stream().filter(At.Companion::class.java::isInstance).findFirst().orElse(null) as At
            val target = bot.getContactOrNull(at.target)
        }
    }

    private fun onReply() {
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            val msg = it.message.contentToString()
            //模糊匹配回复消息
            replyMessageFuzzy.keys.forEach { fuck ->
                if (msg.contains(fuck)) {
                    val reply = replyMessageFuzzy[fuck]
                    group.sendMessage(message.quote() + reply!!)
                }
            }

            //精确匹配回复
            replyMessageAccurate.forEach { (fuckMsg, replyFuckMsg) ->
                if (msg == fuckMsg) {
                    group.sendMessage(message.quote() + replyFuckMsg)
                }
            }
        }
    }

    private fun onAtEvent() {
    }
}

@Serializable
data class RconServer(val ip: String, val port: Int, val password: String)

@Serializable
data class MinecraftServer(val ip: String, val port: Int)

@Serializable
data class Player(val qqId: Long, val gameId: String)

/**
 * ## 插件内部数据
 */
object PluginData : AutoSavePluginData("HkeBot") {
    //服务器信息
    var rconServerInfo: RconServer by value(RconServer("localhost", 25565, "111111"))

    //已经在白名单内的
    var playerInfoAlready: MutableMap<Long, Player> by value()

    //未在白名单内的
    var playerInfoUnready: MutableMap<Long, Player> by value()
}

object PluginSignData :
    AutoSavePluginData("HkeBot-" + SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis())) {
    var signList: MutableList<Long> by value()
}

object PluginConfig : AutoSavePluginConfig("HkeBot") {
    var mcServerInfo: MinecraftServer by value(MinecraftServer("mc.hypixel.net", 25565))
    var motdMessage: String by value("motd:{motd} \n ip地址:{ip} \n 端口:{port} \n 目前在线人数:{players}")
    var muteTexts: MutableList<String> by value(arrayListOf("草"))
    var recallTexts: MutableList<String> by value(arrayListOf("wdnmd"))
    var muteTime: Int by value(60)
    var moneyGive: Int by value(1)
    var joinGrouMessage: String by value("欢迎来到{group}！")
    var replyMessageFuzzy: MutableMap<String, String> by value(mutableMapOf("你好" to "你好呀"))
    var replyMessageAccurate: MutableMap<String, String> by value(mutableMapOf("qwq" to "qwq"))
    var banList: MutableList<Long> by value(arrayListOf())
}

/**-
 * ## 插件复合命令
 */
object PluginCompositeCommand : CompositeCommand(HkeBot, "hkebot", secondaryNames = arrayOf("#")) {
    @SubCommand
    @Description("向服务器发送远程命令")
    suspend fun CommandSender.cmd(vararg command: String) {
        sendMessage(HkeBot.sendCommand(command.toCommand()))
    }

    @SubCommand
    @Description("修改MC服务器[非Rcon]")
    suspend fun CommandSender.setServer(name: String, ip: String, port: Int) {
        mcServerInfo = MinecraftServer(ip, port)
        sendMessage("Add $name server successfully")
    }

    @SubCommand
    @Description("查询服务器信息")
    suspend fun CommandSender.list() {
        kotlin.runCatching {
            val ip = mcServerInfo.ip
            val port = mcServerInfo.port
            val result = MCPing.getPing(ip)
            result
        }.onSuccess { res ->
            sendMessage(
                motdMessage.replace("{ip}", res.hostname).replace("{port}", res.port.toString())
                    .replace("{players}", res.players.online.toString()).replace("{motd}", res.description.strippedText)
            )
        }.onFailure {
            bot!!.logger.info(it)
        }
    }

    @SubCommand
    @Description("申请白名单")
    suspend fun CommandSender.申请白名单(游戏名: String) {
        if (playerInfoUnready.containsKey(user!!.id)) {
            sendMessage("你已经申请过白名单了")
            return@申请白名单
        }
        if (playerInfoAlready.containsKey(user!!.id)) {
            sendMessage("你已经在白名单内")
            return@申请白名单
        }
        val pattern = Pattern.compile("^[\\u4e00-\\u9fa5]{1,9}$|^[\\dA-Za-z_]{1,16}$")
        if (!pattern.matcher(游戏名).matches()) {
            sendMessage("游戏名不符合要求！")
            return@申请白名单
        }
        playerInfoUnready[user!!.id] = Player(user!!.id, 游戏名)
        sendMessage("申请成功，请等待管理员审核")
    }

    @SubCommand
    @Description("同意白名单申请")
    suspend fun CommandSender.同意(游戏名: Long) {
        if (playerInfoUnready.containsKey(游戏名)) {
            playerInfoAlready[游戏名] = playerInfoUnready[游戏名]!!
            playerInfoUnready.remove(游戏名)
            sendMessage("同意成功")
        } else {
            sendMessage("QQ: $游戏名 没有申请")
        }
    }


    @SubCommand
    @Description("签到")
    suspend fun CommandSender.签到() {
        if (signList.contains(user!!.id)) {
            sendMessage("你已经签到过了")
            return
        }
        if (!playerInfoAlready.containsKey(user!!.id)) {
            sendMessage("你还不在白名单内")
            return
        }
        if (!playerInfoUnready.containsKey(user!!.id)) {
            sendMessage("你还没有申请白名单")
            return
        }
        signList.add(user!!.id)
        val command = "eco give ${user!!.id} ${PluginConfig.moneyGive}"
        sendMessage(HkeBot.sendCommand(command))
        sendMessage("签到成功")
    }

    @SubCommand
    @Description("解绑")
    suspend fun CommandSender.解绑() {
        if (!playerInfoAlready.containsKey(user!!.id)) {
            sendMessage("你还不在白名单内")
            return
        }
        playerInfoAlready.remove(user!!.id)
        sendMessage("解绑成功")
    }
}
