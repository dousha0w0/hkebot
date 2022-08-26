package me.dousha
import net.mamoe.mirai.event.AbstractEvent

/**
 * ## 重命名事件
 *
 * 用于插件之间联动重命名
 *
 * @property pluginId 插件ID
 * @property oldName 旧名称
 * @property newName 新名称
 */
data class RenameEvent(val pluginId: String, val oldName: String, val newName: String) : AbstractEvent()
