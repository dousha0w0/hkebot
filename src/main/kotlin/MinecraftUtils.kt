/**
 * 以前面加空格的方式拼接成命令
 *
 * @return
 */
package me.dousha
fun Array<out String>.toCommand(): String {
    var command = ""
    forEach {
        command += " $it"
    }
    return command.trim()
}