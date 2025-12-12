package cn.yurin.mcl.ui.page.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.yurin.mcl.core.Account
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.core.login
import cn.yurin.mcl.ui.localization.*
import cn.yurin.mcl.ui.localization.destination.AccountsDest
import cn.yurin.mcl.ui.localization.destination.cancel
import cn.yurin.mcl.ui.localization.destination.content
import cn.yurin.mcl.ui.localization.destination.login
import cn.yurin.mcl.ui.localization.destination.loginAccount
import cn.yurin.mcl.ui.localization.destination.offlineAccount
import cn.yurin.mcl.ui.localization.destination.onlineAccount
import cn.yurin.mcl.ui.localization.destination.title
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI

@Composable
context(_: Context, _: Data)
fun Accounts() = dest(AccountsDest) {
	Row {
		Sidebar(
		)
		Content(
		)
	}
}

@Composable
context(context: Context, data: Data)
private fun RowScope.Sidebar() = dest(AccountsDest.SideBar) {
	var showDialog by remember { mutableStateOf(false) }
	var job by remember { mutableStateOf<Job?>(null) }
	NavigationRail(
		containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.2F),
	) {
		Spacer(
			modifier = Modifier.height(16.dp),
		)
		Text(
			text = loginAccount.current,
			color = MaterialTheme.colorScheme.onSurface,
			style = MaterialTheme.typography.titleLarge,
		)
		Spacer(
			modifier = Modifier.height(16.dp),
		)
		HorizontalDivider(
			modifier = Modifier.padding(horizontal = 16.dp),
		)
		Spacer(
			modifier = Modifier.height(16.dp),
		)
		FilledTonalButton(
			onClick = { showDialog = true },
			shape = RoundedCornerShape(12.dp),
			colors = ButtonDefaults.filledTonalButtonColors(
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary,
			),
		) {
			Text(
				text = onlineAccount.current,
				color = MaterialTheme.colorScheme.onPrimary,
				style = MaterialTheme.typography.bodyLarge,
			)
		}
		FilledTonalButton(
			onClick = {

			},
			shape = RoundedCornerShape(12.dp),
			colors = ButtonDefaults.filledTonalButtonColors(
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary,
			),
		) {
			Text(
				text = offlineAccount.current,
				color = MaterialTheme.colorScheme.onPrimary,
				style = MaterialTheme.typography.bodyLarge,
			)
		}
		Spacer(
			modifier = Modifier.height(16.dp),
		)
	}
	if (showDialog) {
		dest(AccountsDest.LoginDialog) {
			AlertDialog(
				onDismissRequest = { },
				title = {
					Text(
						text = title.current,
						color = MaterialTheme.colorScheme.onSurface,
						style = MaterialTheme.typography.titleLarge,
					)
				},
				icon = null,
				confirmButton = {
					TextButton(
						onClick = {
							job = data.scope.launch {
								login(
									onLoginRequest = { loginUserCode, loginVerificationUri ->
										Desktop.getDesktop().browse(URI(loginVerificationUri))
										Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(loginUserCode), null)
									}
								)?.let {
									showDialog = false
									data.accounts += it
									data.currentAccount = it
								}
							}
						},
					) {
						Text(
							text = login.current,
							color = MaterialTheme.colorScheme.onSurface,
							style = MaterialTheme.typography.titleSmall,
						)
					}
				},
				dismissButton = {
					TextButton(
						onClick = {
							showDialog = false
							job?.cancel()
						},
					) {
						Text(
							text = cancel.current,
							color = MaterialTheme.colorScheme.onSurface,
							style = MaterialTheme.typography.titleSmall,
						)
					}
				},
				text = {
					Text(
						text = content.current,
						color = MaterialTheme.colorScheme.onSurface,
						style = MaterialTheme.typography.bodyLarge,
					)
				},
			)
		}
	}
}

@Composable
context(_: Context, data: Data)
private fun RowScope.Content(
) = dest(AccountsDest.Content) {
	Column(
		modifier = Modifier
			.weight(0.8F)
			.fillMaxHeight()
			.padding(horizontal = 32.dp)
			.verticalScroll(rememberScrollState()),
	) {
		Spacer(
			modifier = Modifier.height(32.dp),
		)
		AnimatedContent(data.accounts) { accounts ->
			accounts.forEach { account ->
				Card(
					account = account,
				)
				Spacer(
					modifier = Modifier.height(32.dp),
				)
			}
		}
	}
}

@Composable
context(_: Context, data: Data)
private fun Card(
	account: Account,
) = dest(AccountsDest.Content) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.background(
				animateColorAsState(
					when (data.currentAccount == account) {
						true -> MaterialTheme.colorScheme.primary
						else -> MaterialTheme.colorScheme.surfaceContainer
					}
				).value
			)
			.clickable { data.currentAccount = account }
			.padding(16.dp),
	) {
		Text(
			text = account.name,
			color = animateColorAsState(
				when (data.currentAccount == account) {
					true -> MaterialTheme.colorScheme.onPrimary
					else -> MaterialTheme.colorScheme.onSurface
				}
			).value,
			style = MaterialTheme.typography.titleLarge,
		)
		Text(
			text = when (account) {
				is Account.Online -> onlineAccount.current
				is Account.Offline -> offlineAccount.current
			},
			color = animateColorAsState(
				when (data.currentAccount == account) {
					true -> MaterialTheme.colorScheme.onPrimary
					else -> MaterialTheme.colorScheme.onSurface
				}
			).value,
			style = MaterialTheme.typography.bodyLarge,
		)
	}
}