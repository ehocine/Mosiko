package com.hocel.mosiko.ui.components.musicompose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.hocel.mosiko.model.MusicControllerState
import com.hocel.mosiko.ui.components.SetTimerSlider
import com.hocel.mosiko.ui.theme.*
import com.hocel.mosiko.utils.AlarmUtil
import com.hocel.mosiko.utils.AppUtils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.hocel.mosiko.R

@OptIn(
    ExperimentalUnitApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun MusicScreenSetTimerSheetContent(
    scope: CoroutineScope,
    musicControllerState: MusicControllerState
) {

    val context = LocalContext.current

    var timeInMinute by remember { mutableStateOf(0f) }
    var minute by remember { mutableStateOf(0L) }

    minute = timeInMinute.toString().substring(
        0,
        if (timeInMinute < 10) 1 else 2
    ).toLong()

    val setTimeToMinuteText = buildAnnotatedString {
        if (minute == 0L) {
            append("${stringResource(id = R.string.set_time_to)}: ")

            withStyle(
                typographySkModernist().body1.copy(
                    color = sunset_orange,
                    fontSize = TextUnit(16f, TextUnitType.Sp)
                ).toSpanStyle()
            ) {
                append(stringResource(id = R.string.non_active))
            }
        } else {
            append("${stringResource(id = R.string.set_time_to)}: ")

            withStyle(
                typographySkModernist().body1.copy(
                    color = sunset_orange,
                    fontSize = TextUnit(16f, TextUnitType.Sp)
                ).toSpanStyle()
            ) {
                append(minute.toString())
            }

            append(" mnt")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        musicControllerState.setTimerModalBottomSheetState.hide()
                        delay(200)
                        musicControllerState.musicMoreOptionModalBottomSheetState.show()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    tint = if (isSystemInDarkTheme()) white else black,
                    contentDescription = null
                )
            }

            Text(
                text = stringResource(id = R.string.set_timer),
                textAlign = TextAlign.Center,
                style = typographyDmSans().body1.copy(
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .weight(1f)
            )

            IconButton(
                onClick = {
                    if (minute == 0L) {
                        AlarmUtil.cancelTimer(context)
                        context.getString(R.string.timer_disabled).toast(context)
                    } else {
                        AlarmUtil.setTimer(
                            context = context,
                            durationInMs = minute * 60 * 1000
                        )

                        "${context.getString(R.string.timer_is_set_to)} $minute mnt".toast(context)
                    }

                    scope.launch {
                        musicControllerState.setTimerModalBottomSheetState.hide()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    tint = if (isSystemInDarkTheme()) white else black,
                    contentDescription = null
                )
            }
        }



        Text(
            text = setTimeToMinuteText,
            modifier = Modifier
                .padding(
                    start = 14.dp,
                    end = 14.dp,
                )
        )



        SetTimerSlider(
            value = timeInMinute,
            onValueChange = { mTimeInMinute ->
                timeInMinute = mTimeInMinute
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 14.dp,
                    end = 14.dp,
                    bottom = 24.dp
                )
        )
    }
}
