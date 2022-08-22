package me.dousha

import Rcon
import RenameEvent
import br.com.azalim.mcserverping.MCPing
import br.com.azalim.mcserverping.MCPingOptions
import kotlinx.serialization.Serializable
import me.dousha.PluginConfig.mcServerInfo
import me.dousha.PluginData.serverInfo
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.id
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.console.util.ContactUtils.getContactOrNull
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.utils.info
import toCommand
import java.io.IOException


object HkeBot : KotlinPlugin(JvmPluginDescription(
    id = "me.dousha.hkebot",
    name = "HkeBot",
    version = "0.1.0",
) {
    author("dousha")
}) {
    private val rconList: MutableMap<String, Rcon> = mutableMapOf()


    override fun onEnable() {
        PluginData.reload()
        PluginConfig.reload()
        PluginCompositeCommand.register()
        serverInfo.forEach {
            openRcon(it.key, it.value.ip, it.value.port, it.value.password)
        }
        logger.info { "Plugin loaded" }
        globalEventChannel().subscribeAlways<RenameEvent> {
            logger.info("RenameEvent: pluginId = $pluginId oldName = $oldName newName = $newName")
            if (pluginId == HkeBot.id) return@subscribeAlways
            PluginCompositeCommand.renameServer(oldName, newName, true)
        }
    }

    override fun onDisable() {
        rconList.forEach { it.value.close() }
    }

    fun openRcon(name: String, ip: String, port: Int, password: String): Boolean {
        kotlin.runCatching {
            Rcon.open(ip, port)
        }.onSuccess { rcon ->
            if (!rcon.authenticate(password)) {
                logger.error("$name 验证失败,请检查密码是否正确")
                rcon.close()
            } else {
                rconList[name] = rcon
                return true
            }
        }.onFailure {
            logger.error(it)
        }
        return false
    }

    fun sendCommand(name: String, command: String): String {
        return try {
            rconList[name]?.sendCommand(command) ?: "Server $name does not exist or did not connect successfully"
        } catch (e: IOException) {
            "The remote server $name is down!!!"
        }
    }

    fun rename(name: String, newName: String): Boolean {
        rconList[name]?.let {
            rconList[name] = it
            return true;
        }
        return false
    }

    fun delete(name: String): Boolean {
        rconList[name]?.let {
            it.close()
            return rconList.remove(name) != null
        }
        return false
    }


    /**
     * 发送信息事件
     */
    fun onMessage() {
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            if (message[0].contentEquals("申请白名单")) {
                // TODO: 将玩家加入到白名单
            } else if (message[0].contentEquals("改绑")) {
                // TODO:将QQ和游戏ID改绑定
            } else if (message.contentEquals("签到")) {
                // TODO: 给游戏内发送货币
            }
            // TODO: 关键词回复

            // TODO: 用指令把人踢了，并加入黑名单

            // TODO: 用指令查服务器的MOTD,在线人数
        }
    }

    @OptIn(ConsoleExperimentalApi::class)
    fun onJoinGroup() {
        GlobalEventChannel.subscribeAlways<MemberJoinEvent> {
            member.sendMessage("欢迎加入${member.group.name}")
        }
        GlobalEventChannel.subscribeAlways<MemberJoinRequestEvent> {
            val user = bot.getContactOrNull(fromId) as User
            //大于15级别的QQ可以加入群
            val qLevel = user.queryProfile().qLevel
            if (qLevel >= 15) {
                accept()
            }
        }
    }

    fun onMuteEvent() {
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            // TODO: 从配置文件中检测文字，并撤回

            // TODO: 从配置文件中检测文字，并撤回
        }
    }

}

@Serializable
data class RconServer(val ip: String, val port: Int, val password: String)

@Serializable
data class MinecraftServer(val ip: String, val port: Int)

/**
 * ## 插件内部数据
 */
object PluginData : AutoSavePluginData("HkeBot") {
    val serverInfo: MutableMap<String, RconServer> by value()
}

object PluginConfig : AutoSavePluginConfig("HkeBot") {
    val mcServerInfo: MutableMap<String, MinecraftServer> by value()
    val motdMessage = "服务器名称%server%\n目前人数%online%,motd:%motd%"
}

/**
 * ## 插件复合命令
 */
object PluginCompositeCommand : CompositeCommand(HkeBot, "rcon") {
    @SubCommand
    @Description("向服务器发送远程命令")
    suspend fun CommandSender.cmd(name: String, vararg command: String) {
        sendMessage(HkeBot.sendCommand(name, command.toCommand()))
    }

    @SubCommand
    @Description("添加服务器")
    suspend fun CommandSender.add(name: String, ip: String, port: Int, password: String) {
        if (HkeBot.openRcon(name, ip, port, password)) {
            serverInfo[name] = RconServer(ip, port, password)
            sendMessage("Add $name server successfully")
        } else sendMessage("Add $name server failed,check the logs for the specific reason")
    }

    @SubCommand
    @Description("删除服务器")
    suspend fun CommandSender.delete(name: String) {
        if (serverInfo.remove(name) != null) {
            HkeBot.delete(name)
            sendMessage("Delete $name server successfully")
        } else sendMessage("Server does not exist")
    }

    @SubCommand
    @Description("重命名服务器")
    suspend fun CommandSender.rename(name: String, newName: String) {
        sendMessage(renameServer(name, newName, false))
    }

    suspend fun renameServer(name: String, newName: String, isEvent: Boolean): String {
        serverInfo[name].let {
            if (it == null) {
                return "Server does not exist"
            }
            serverInfo[newName] = it
            serverInfo.remove(name)
            return if (HkeBot.rename(name, newName)) {
                // 不是事件就发布改名广播
                if (!isEvent) RenameEvent(HkeBot.id, name, newName).broadcast()
                "Server rename successful: $name -> $newName"
            } else "Server $name not connected successfully, rename failed"
        }
    }

    @SubCommand
    @Description("服务器列表")
    suspend fun CommandSender.servers() {
        if (serverInfo.isEmpty()) {
            sendMessage("Current server list is empty")
            return
        }
        var list = "The server list is as follows:"
        serverInfo.forEach {
            list += "\n${it.key} : ${it.value.ip} ${it.value.port}"
        }
        sendMessage(list)
    }

    @SubCommand
    @Description("添加MC服务器[非Rcon]")
    suspend fun CommandSender.addMcServer(name: String, ip: String, port: Int) {
        mcServerInfo[name] = MinecraftServer(ip, port)
        sendMessage("Add $name server successfully")
    }

    @SubCommand
    @Description("查询服务器信息")
    suspend fun CommandSender.list(name: String) {
        mcServerInfo.getOrElse(name) {
            sendMessage("Server $name not found")
            return
        }.let {
            val ip = it.ip
            println(ip)
            val port = it.port
            println(port)

//            val options = MCPingOptions.builder()
//                .hostname("mc.hypixel.net")
//                .build()
//            val result = MCPing.getPing(options)
//            println(result.players.online)
        }
    }


}
