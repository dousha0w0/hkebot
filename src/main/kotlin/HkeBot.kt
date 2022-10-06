package me.dousha

import br.com.azalim.mcserverping.MinecraftServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import me.dousha.HkeBot.PluginConfig.banList
import me.dousha.HkeBot.PluginConfig.joinGrouMessage
import me.dousha.HkeBot.PluginConfig.mcServerInfo
import me.dousha.HkeBot.PluginConfig.moneyGive
import me.dousha.HkeBot.PluginConfig.motdMessage
import me.dousha.HkeBot.PluginConfig.muteTexts
import me.dousha.HkeBot.PluginConfig.muteTime
import me.dousha.HkeBot.PluginConfig.recallTexts
import me.dousha.HkeBot.PluginConfig.replyMessageAccurate
import me.dousha.HkeBot.PluginConfig.replyMessageFuzzy
import me.dousha.HkeBot.PluginData.playerInfoAlready
import me.dousha.HkeBot.PluginData.playerInfoUnready
import me.dousha.HkeBot.PluginData.rconServerInfo
import me.dousha.HkeBot.PluginSignData.signList
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
import net.mamoe.mirai.console.util.sendAnsiMessage
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import net.mamoe.mirai.message.data.findIsInstance
import net.mamoe.mirai.message.data.toMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.info
import org.zrnq.mclient.FONT
import org.zrnq.mclient.dnsServerList
import org.zrnq.mclient.output.APIOutputHandler
import org.zrnq.mclient.pingInternal
import org.zrnq.mclient.renderInfoImage
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.imageio.ImageIO


object HkeBot : KotlinPlugin(JvmPluginDescription(
    id = "me.dousha.hkebot",
    name = "HkeBot",
    version = "0.1.0",
) {
    author("dousha")
}) {
    private var rconServer: Rcon? = null
    override fun onEnable() {
        PluginData.reload()
        PluginSignData.reload()
        PluginConfig.reload()
        PluginCompositeCommand.register()
        FONT = Font("Microsoft YaHei UI", Font.PLAIN, 20)
        dnsServerList = listOf("223.5.5.5", "8.8.8.8", "114.114.114.114")
        openRcon(rconServerInfo.ip, rconServerInfo.port, rconServerInfo.password)
        logger.info {
            "\n" + " __    __   __  ___  _______ .______     ______   .___________.\n" + "|  |  |  | |  |/  / |   ____||   _  \\   /  __  \\  |           |\n" + "|  |__|  | |  '  /  |  |__   |  |_)  | |  |  |  | `---|  |----`\n" + "|   __   | |    <   |   __|  |   _  <  |  |  |  |     |  |     \n" + "|  |  |  | |  .  \\  |  |____ |  |_)  | |  `--'  |     |  |     \n" + "|__|  |__| |__|\\__\\ |_______||______/   \\______/      |__|     \n" + "                                                               \n"
        }
        onJoinGroup()
        onReply()
        onMuteEvent()
        onChatEvent()
    }

    override fun onDisable() {
        rconServer!!.close()
    }

    private fun openRcon(ip: String, port: Int, password: String): Boolean {
        kotlin.runCatching {
            rconServer = Rcon.open(ip, port)
            rconServer
        }.onSuccess { rcon ->
            if (!rcon!!.authenticate(password)) {
                logger.error("验证失败,请检查密码是否正确")
                rcon.close()
            } else {
                logger.info("Rcon连接成功")
                return true
            }
        }.onFailure {
            logger.error("Rcon连接失败,请检查IP和端口是否正确")
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


    private suspend fun CommandSender.doPing(target : String) = withContext(Dispatchers.IO) {
        var error : String? = null
        var image : BufferedImage? = null
        pingInternal(target, APIOutputHandler(HkeBot.logger, { error = it }, { image = it }), false)
        if(image == null)
            reply(error!!)
        else
            reply(image!!)
    }

    private suspend fun CommandSender.reply(message : String) {
        if(user == null) sendAnsiMessage { lightPurple().append(message) }
        else sendMessage(At(user!!.id) + message)
    }

    private suspend fun CommandSender.reply(image : BufferedImage) {
        if(user == null) {
            val savePath = File("${UUID.randomUUID()}.png")
            withContext(Dispatchers.IO) { ImageIO.write(image, "png", savePath) }
            reply("查询结果已保存至${savePath.absolutePath}")
        } else {
            val bis = ByteArrayOutputStream()
            withContext(Dispatchers.IO) { ImageIO.write(image, "png", bis) }
            ByteArrayInputStream(bis.toByteArray()).toExternalResource("png").use {
                sendMessage(At(user!!.id) + user!!.uploadImage(it))
            }
        }
    }

    @OptIn(ConsoleExperimentalApi::class)
    private fun onChatEvent() {
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            if (message.contentToString().startsWith("改绑")) {
                val at = message.toMessageChain().findIsInstance<At>()
                val target = bot.getContactOrNull(at!!.target)
                if (playerInfoAlready.containsKey(target!!.id)) {
                    //提取消息最后文本
                    val name = message.contentToString().split(" ").last()
                    val pattern = Pattern.compile("^[\\u4e00-\\u9fa5]{1,9}$|^[\\dA-Za-z_]{1,16}$")
                    if (pattern.matcher(name).matches()) {
                        playerInfoAlready[target.id] = Player(target.id, name)
                        group.sendMessage(message.quote() + "已成功为${target}改绑")
                    } else {
                        group.sendMessage("请输入正确的玩家名字")
                    }
                } else {
                    group.sendMessage(message.quote() + "此玩家不在白名单内")
                }
            }
        }

        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            if (message.contentToString().startsWith("申请白名单")) {
                val playerId = message.contentToString().split(" ").last()
                val gameId = message.contentToString().split(" ")[1]
                if (playerInfoUnready.containsKey(sender.id)) {
                    group.sendMessage(
                        message.quote() + "你已经申请过白名单了"
                    )
                    return@subscribeAlways
                }
                if (playerInfoAlready.containsKey(sender.id)) {
                    group.sendMessage(message.quote() + "你已经在白名单内！")
                    return@subscribeAlways
                }
                val pattern = Pattern.compile("^[\\u4e00-\\u9fa5]{1,9}$|^[\\dA-Za-z_]{1,16}$")
                if (!pattern.matcher(gameId).matches()) {
                    group.sendMessage(message.quote() + "游戏名不符合要求！")
                    return@subscribeAlways
                }
                playerInfoAlready[sender.id] = Player(sender.id, gameId)
                group.sendMessage(message.quote() + "申请成功！")
            }

            if (message.contentToString().startsWith("#list")) {
                kotlin.runCatching {
                    val ip = mcServerInfo.ip
                    val port = mcServerInfo.port
                    val image = renderInfoImage(ip, port)
                    val minecraftServer1 = br.com.azalim.mcserverping.MinecraftServer(ip, port)
                    minecraftServer1.fetchData()
//                    minecraftServer1
                    image
                }.onSuccess { res ->
                    //send bufferedimage
                    val bis = ByteArrayOutputStream()
                    withContext(Dispatchers.IO) { ImageIO.write(res, "png", bis) }
                    ByteArrayInputStream(bis.toByteArray()).toExternalResource("png").use {
                        group.sendMessage(sender.uploadImage(it))
                    }
//                    group.sendMessage(
//                        message.quote() + motdMessage.replace("{ip}", res.address)
//                            .replace("{port}", res.port.toString()).replace("{players}", res.playersOnline.toString())
//                            .replace("{motd}", res.motd)
//                            .replace("{maxplayers}", res.maxPlayers.toString())
//                    )
                }.onFailure {
                    bot.logger.info(it)
                    group.sendMessage(message.quote() + "服务器连接失败")
                }
            }

            if (message.contentToString().startsWith("踢黑")) {
                if (!sender.isOperator()) {
                    group.sendMessage(message.quote() + "您没有权限")
                    return@subscribeAlways
                }
                val playerId = message.contentToString().split(" ").last()
                val at = message.stream().filter(At.Companion::class.java::isInstance).findFirst().orElse(null) as At
                val target = bot.getContactOrNull(at.target)
                if (target != null) {
                    if (playerInfoAlready.containsKey(target.id)) {
                        playerInfoAlready.remove(target.id)
                    }
                    banList.add(target.id)
                    group.sendMessage(message.quote() + "已成功将 ${group.getMember(target.id)!!.nameCardOrNick} 踢黑")
                    group.getMember(target.id)!!.kick("你已被管理员踢黑")
                } else {
                    group.sendMessage(message.quote() + "此玩家不在白名单内")
                }
            } else if (message.contentToString().startsWith("删黑")) {
                val target = bot.getContactOrNull(message.contentToString().split(" ").last().toLong())
                if (target != null) {
                    if (banList.contains(target.id)) {
                        banList.remove(target.id)
                        group.sendMessage(message.quote() + "已成功将 ${group.getMember(target.id)!!.nameCardOrNick} 删黑")
                    } else {
                        group.sendMessage(message.quote() + "此玩家不在黑名单内")
                    }
                } else {
                    group.sendMessage(message.quote() + "没找到此QQ")
                }
            }

            if (message.contentToString().startsWith("签到")) {
                if (signList.contains(sender.id)) {
                    group.sendMessage(message.quote() + "您已经签到过了")
                    return@subscribeAlways
                }
                group.sendMessage(message.quote() + sendCommand("eco give ${sender.id} $moneyGive"))
                signList.add(sender.id)
            }
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
    object PluginCompositeCommand : CompositeCommand(HkeBot, "rcon", secondaryNames = arrayOf("#")) {
        @SubCommand
        @Description("向服务器发送远程命令")
        suspend fun CommandSender.cmd(vararg command: String) {
            try {
                sendMessage(sendCommand(command.toCommand()))
                logger.info("向服务器发送远程命令：${command.toCommand()}")
            } catch (e: Exception) {
            }
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
                println("ip:$ip port:$port")
                val minecraftServer1 = br.com.azalim.mcserverping.MinecraftServer(ip, port)
                minecraftServer1.fetchData()
//                val mcPingOptions = MCPingOptions.builder().hostname(ip).port(port).build()
//                val result = MCPing.getPing(mcPingOptions)
                minecraftServer1
            }.onSuccess { res ->
                sendMessage(
                    motdMessage.replace("{ip}", res.address).replace("{port}", res.port.toString())
                        .replace("{players}", res.playersOnline.toString())
                        .replace("{motd}", res.motd)
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
            kotlin.runCatching {
                sendMessage(sendCommand(command))
            }.onSuccess {
                sendMessage("签到成功")
            }
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
}
