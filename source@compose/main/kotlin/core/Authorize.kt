package cn.yurin.mcl.core

import cn.yurin.mcl.core.Either.Left
import cn.yurin.mcl.core.Either.Right
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

private val json = Json {
	ignoreUnknownKeys = true
}

private suspend inline fun <reified T> HttpResponse.body(json: Json): T = json.decodeFromString(bodyAsText())

context(data: Data)
suspend fun getDeviceCode(): DeviceCodeResponse = data.client.post("https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode") {
	contentType(ContentType.Application.FormUrlEncoded)
	accept(ContentType.Application.Json)
	setBody(
		buildString {
			append("client_id", "=", "ede30e06-1d14-433a-9b50-d99002db8899", "&")
			append("scope", "=", "XboxLive.signin")
		}
	)
}.body(json)

context(data: Data)
suspend fun checkAuthorizationStatus(deviceCode: String): AuthorizationStatus = data.client.post("https://login.microsoftonline.com/consumers/oauth2/v2.0/token") {
	contentType(ContentType.Application.FormUrlEncoded)
	accept(ContentType.Application.Json)
	setBody(
		buildString {
			append("grant_type", "=", "urn:ietf:params:oauth:grant-type:device_code", "&")
			append("client_id", "=", "ede30e06-1d14-433a-9b50-d99002db8899", "&")
			append("device_code", "=", deviceCode)
		}
	)
}.body(json)

context(data: Data)
suspend fun xblAuthorization(accessToken: String): XboxLiveAuthorizationResponse = data.client.post("https://user.auth.xboxlive.com/user/authenticate") {
	contentType(ContentType.Application.Json)
	accept(ContentType.Application.Json)
	setBody(
		buildJsonObject {
			putJsonObject("Properties") {
				put("AuthMethod", "RPS")
				put("SiteName", "user.auth.xboxlive.com")
				put("RpsTicket", "d=$accessToken")
			}
			put("RelyingParty", "http://auth.xboxlive.com")
			put("TokenType", "JWT")
		}
	)
}.body(json)

context(data: Data)
suspend fun xstsAuthorization(xblToken: String): XboxLiveAuthorizationResponse = data.client.post("https://xsts.auth.xboxlive.com/xsts/authorize") {
	contentType(ContentType.Application.Json)
	accept(ContentType.Application.Json)
	setBody(
		buildJsonObject {
			putJsonObject("Properties") {
				put("SandboxId", "RETAIL")
				putJsonArray("UserTokens") {
					add(xblToken)
				}
			}
			put("RelyingParty", "rp://api.minecraftservices.com/")
			put("TokenType", "JWT")
		}
	)
}.body(json)

context(data: Data)
suspend fun getMinecraftAccessToken(xstsToken: String, uhs: String): MinecraftAccessTokenResponse = data.client.post("https://api.minecraftservices.com/authentication/login_with_xbox") {
	contentType(ContentType.Application.Json)
	accept(ContentType.Application.Json)
	setBody(
		buildJsonObject {
			put("identityToken", "XBL3.0 x=$uhs;$xstsToken")
		}
	)
}.body(json)

context(data: Data)
suspend fun checkHasMinecraft(accessToken: String): HasMinecraftResponse = data.client.get("https://api.minecraftservices.com/entitlements/mcstore") {
	accept(ContentType.Application.Json)
	headers {
		append("Authorization", "Bearer $accessToken")
	}
}.body(json)

context(data: Data)
suspend fun getMinecraftProfile(accessToken: String): MinecraftProfileResponse = data.client.get("https://api.minecraftservices.com/minecraft/profile") {
	accept(ContentType.Application.Json)
	headers {
		append("Authorization", "Bearer $accessToken")
	}
}.body(json)

@Serializable
data class DeviceCodeResponse(
	@SerialName("user_code")
	val userCode: String,
	@SerialName("device_code")
	val deviceCode: String,
	@SerialName("verification_uri")
	val verificationUri: String,
	@SerialName("expires_in")
	val expiresIn: Int,
	val interval: Int,
	val message: String,
)

@Serializable(with = AuthorizationStatusSerializer::class)
sealed class AuthorizationStatus {
	@Serializable
	data class Success(
		@SerialName("token_type")
		val tokenType: String,
		val scope: String,
		@SerialName("expires_in")
		val expiresIn: Int,
		@SerialName("ext_expires_in")
		val extExpiresIn: Int,
		@SerialName("access_token")
		val accessToken: String,
		@SerialName("refresh_token")
		val refreshToken: String? = null,
		@SerialName("id_token")
		val idToken: String? = null,
	) : AuthorizationStatus()

	@Serializable
	data class Failure(
		val error: String,
		@SerialName("error_description")
		val errorDescription: String,
		@SerialName("error_codes")
		val errorCodes: List<Int>,
		val timestamp: String,
		@SerialName("error_uri")
		val errorUri: String,
	) : AuthorizationStatus()
}

@Serializable
data class XboxLiveAuthorizationResponse(
	@SerialName("IssueInstant")
	val issueInstant: String,
	@SerialName("NotAfter")
	val notAfter: String,
	@SerialName("Token")
	val token: String,
	@SerialName("DisplayClaims")
	val displayClaims: DisplayClaims,
) {
	@Serializable
	data class DisplayClaims(
		val xui: List<Xui>,
	) {
		@Serializable
		data class Xui(
			val uhs: String,
		)
	}
}

@Serializable
data class MinecraftAccessTokenResponse(
	val username: String,
	val roles: List<String>,
	@SerialName("access_token")
	val accessToken: String,
	@SerialName("token_type")
	val tokenType: String,
	@SerialName("expires_in")
	val expiresIn: Int,
)

@Serializable
data class HasMinecraftResponse(
	val items: List<Item>,
	val signature: String,
	val keyId: Int,
) {
	@Serializable
	data class Item(
		val name: String,
		val signature: String,
	)
}

@Serializable
data class MinecraftProfileResponse(
	val id: String,
	val name: String,
	val skins: List<Skin>,
	val capes: List<Cape>,
) {
	@Serializable
	data class Skin(
		val id: String,
		val state: String,
		val url: String,
		val variant: String,
		val alias: String? = null,
		val textureKey: String,
	)

	@Serializable
	data class Cape(
		val id: String,
		val state: String,
		val url: String,
		val alias: String,
	)
}

@OptIn(InternalSerializationApi::class)
object AuthorizationStatusSerializer : KSerializer<AuthorizationStatus> {
	private val serializer = EitherSerializer(AuthorizationStatus.Success.serializer(), AuthorizationStatus.Failure.serializer())
	override val descriptor: SerialDescriptor
		get() = serializer.descriptor

	override fun serialize(encoder: Encoder, value: AuthorizationStatus) {
		serializer.serialize(
			encoder = encoder,
			value = when (value) {
				is AuthorizationStatus.Success -> Left(value)
				is AuthorizationStatus.Failure -> Right(value)
			}
		)
	}

	override fun deserialize(decoder: Decoder) = when (val value = serializer.deserialize(decoder)) {
		is Left<AuthorizationStatus.Success> -> value.value
		is Right<AuthorizationStatus.Failure> -> value.value
	}
}