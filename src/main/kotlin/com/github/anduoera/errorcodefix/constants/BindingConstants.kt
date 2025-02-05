package com.github.anduoera.errorcodefix.constants

import org.codehaus.jettison.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap


val DEFAULT_RULE_BINDING = mapOf(
    // 比较操作符
    "eq" to "字段值必须等于指定值",
    "ne" to "字段值必须不等于指定值",
    "gt" to "字段值必须大于指定值",
    "gte" to "字段值必须大于或等于指定值",
    "lt" to "字段值必须小于指定值",
    "lte" to "字段值必须小于或等于指定值",
    "eqfield" to "字段值必须等于另一个字段的值",
    "nefield" to "字段值必须不等于另一个字段的值",
    "gtfield" to "字段值必须大于另一个字段的值",
    "gtefield" to "字段值必须大于或等于另一个字段的值",
    "ltfield" to "字段值必须小于另一个字段的值",
    "ltefield" to "字段值必须小于或等于另一个字段的值",

    // 字符串验证
    "contains" to "字符串必须包含指定子串",
    "containsany" to "字符串必须包含指定字符集中的任意字符",
    "containsrune" to "字符串必须包含指定 Unicode 字符",
    "excludes" to "字符串不能包含指定子串",
    "excludesall" to "字符串不能包含指定字符集中的任意字符",
    "excludesrune" to "字符串不能包含指定 Unicode 字符",
    "startswith" to "字符串必须以指定子串开头",
    "endswith" to "字符串必须以指定子串结尾",
    "startsnotwith" to "字符串不能以指定子串开头",
    "endsnotwith" to "字符串不能以指定子串结尾",
    "alpha" to "字符串只能包含字母",
    "alphanum" to "字符串只能包含字母和数字",
    "alphanumunicode" to "字符串只能包含 Unicode 字母和数字",
    "alphaunicode" to "字符串只能包含 Unicode 字母",
    "ascii" to "字符串只能包含 ASCII 字符",
    "lowercase" to "字符串必须为小写",
    "uppercase" to "字符串必须为大写",
    "multibyte" to "字符串必须包含多字节字符",
    "number" to "字符串必须为数字",
    "numeric" to "字符串必须为数字或数字字符串",

    // 格式验证
    "email" to "字段必须为有效的电子邮件地址",
    "url" to "字段必须为有效的 URL",
    "http_url" to "字段必须为有效的 HTTP URL",
    "uri" to "字段必须为有效的 URI",
    "ip" to "字段必须为有效的 IP 地址",
    "ipv4" to "字段必须为有效的 IPv4 地址",
    "ipv6" to "字段必须为有效的 IPv6 地址",
    "mac" to "字段必须为有效的 MAC 地址",
    "uuid" to "字段必须为有效的 UUID",
    "uuid3" to "字段必须为有效的 UUID v3",
    "uuid4" to "字段必须为有效的 UUID v4",
    "uuid5" to "字段必须为有效的 UUID v5",
    "base64" to "字段必须为有效的 Base64 编码字符串",
    "base64url" to "字段必须为有效的 Base64 URL 编码字符串",
    "base64rawurl" to "字段必须为有效的 Base64 Raw URL 编码字符串",
    "hexadecimal" to "字段必须为有效的十六进制字符串",
    "hexcolor" to "字段必须为有效的十六进制颜色代码",
    "rgb" to "字段必须为有效的 RGB 颜色值",
    "rgba" to "字段必须为有效的 RGBA 颜色值",
    "hsl" to "字段必须为有效的 HSL 颜色值",
    "hsla" to "字段必须为有效的 HSLA 颜色值",
    "json" to "字段必须为有效的 JSON 字符串",
    "jwt" to "字段必须为有效的 JWT 令牌",
    "isbn" to "字段必须为有效的 ISBN 编号",
    "isbn10" to "字段必须为有效的 ISBN-10 编号",
    "isbn13" to "字段必须为有效的 ISBN-13 编号",
    "issn" to "字段必须为有效的 ISSN 编号",
    "datetime" to "字段必须为有效的日期时间字符串",
    "timezone" to "字段必须为有效的时区",
    "latitude" to "字段必须为有效的纬度",
    "longitude" to "字段必须为有效的经度",
    "postcode_iso3166_alpha2" to "字段必须为有效的 ISO 3166-1 alpha-2 邮政编码",
    "country_code" to "字段必须为有效的国家代码",

    // 逻辑操作符
    "required" to "字段为必填项",
    "required_if" to "当另一个字段为指定值时，该字段为必填项",
    "required_unless" to "当另一个字段不为指定值时，该字段为必填项",
    "required_with" to "当指定字段存在时，该字段为必填项",
    "required_with_all" to "当所有指定字段存在时，该字段为必填项",
    "required_without" to "当指定字段不存在时，该字段为必填项",
    "required_without_all" to "当所有指定字段不存在时，该字段为必填项",
    "excluded_if" to "当另一个字段为指定值时，该字段必须为空",
    "excluded_unless" to "当另一个字段不为指定值时，该字段必须为空",
    "excluded_with" to "当指定字段存在时，该字段必须为空",
    "excluded_with_all" to "当所有指定字段存在时，该字段必须为空",
    "excluded_without" to "当指定字段不存在时，该字段必须为空",
    "excluded_without_all" to "当所有指定字段不存在时，该字段必须为空",
    "oneof" to "字段值必须为指定值之一",
    "unique" to "字段值必须唯一",

    // 其他
    "omitempty" to "如果字段为空，则忽略该字段",
    "omitnil" to "如果字段为 nil，则忽略该字段",
    "structonly" to "仅验证结构体，不验证字段",
    "nostructlevel" to "不验证结构体级别的规则",
    "isdefault" to "字段值为默认值时忽略验证",
    "len" to "字段长度必须等于指定值",
    "max" to "字段长度必须小于或等于指定值",
    "min" to "字段长度必须大于或等于指定值",
    "dive" to "递归验证字段（用于切片、数组、映射等）",
    "keys" to "验证映射的键",
    "endkeys" to "结束映射键的验证",

    // 加密与哈希
    "md4" to "字段必须为有效的 MD4 哈希值",
    "md5" to "字段必须为有效的 MD5 哈希值",
    "sha256" to "字段必须为有效的 SHA-256 哈希值",
    "sha384" to "字段必须为有效的 SHA-384 哈希值",
    "sha512" to "字段必须为有效的 SHA-512 哈希值",
    "ripemd128" to "字段必须为有效的 RIPEMD-128 哈希值",
    "tiger128" to "字段必须为有效的 Tiger-128 哈希值",
    "tiger160" to "字段必须为有效的 Tiger-160 哈希值",
    "tiger192" to "字段必须为有效的 Tiger-192 哈希值",

    // 特殊用途
    "cron" to "字段必须为有效的 Cron 表达式",
    "semver" to "字段必须为有效的语义化版本号",
    "ulid" to "字段必须为有效的 ULID",
    "cve" to "字段必须为有效的 CVE 编号",
    "dir" to "字段必须为有效的目录路径",
    "file" to "字段必须为有效的文件路径",
    "image" to "字段必须为有效的图像文件路径",
    "iscolor" to "字段必须为有效的颜色值"
)

class AKBindingRuleConstants private constructor() {
    companion object {
        val instance: AKBindingRuleConstants by lazy { AKBindingRuleConstants() }
    }

    private val bindingRulesMap: MutableMap<String, BindingRule> = ConcurrentHashMap()

    fun addBindingRule(key: String, rule: BindingRule) {
        bindingRulesMap[key] = rule
    }

    fun getAllRules(): MutableMap<String, BindingRule> {
        return bindingRulesMap
    }

    fun getRule(key: String): BindingRule {
        return bindingRulesMap[key] ?: BindingRule(
            paramName = "",
            validatorPattern = "",
            validatorValue = "",
            remark = ""
        )
    }

    fun flushedBySettingUri(uri: String) {
        try {
            val connection = URL(uri).openConnection() as HttpURLConnection
            connection.connectTimeout = 1000
            connection.requestMethod = "GET"
            try {
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    bindingRulesMap.clear()
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    println(response)
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val paramName = jsonObject.getString("param_name")

                        val rule = BindingRule(
                            paramName = paramName,
                            validatorPattern = jsonObject.getString("validator_pattern"),
                            validatorValue = jsonObject.getString("validator_value"),
                            remark = jsonObject.getString("remark")
                        )

                        bindingRulesMap[paramName] = rule
                    }
                } else {
                    println("Error: $responseCode")
                }
            } catch (e: Exception) {
                println("Error: $e")
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            println("Invalid URL or other error: ${e.message}")
        }
    }

    fun containsKey(key: String): Boolean {
        return bindingRulesMap.containsKey(key)
    }
}


data class BindingRule(
    val paramName: String,
    val validatorPattern: String,
    val validatorValue: String,
    val remark: String
)
