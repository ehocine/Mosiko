package com.hocel.mosiko.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.hocel.mosiko.R
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.model.Playlist
import com.hocel.mosiko.ui.theme.*
import com.hocel.mosiko.utils.minimumTouchTargetSize
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransparentButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    indication: Indication = rememberRipple(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        elevation = 0.dp,
        shape = shape,
        color = Color.Transparent,
        contentColor = Color.Transparent,
        border = null,
        modifier = modifier
            .then(
                Modifier
                    .clip(shape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = indication,
                        onClick = onClick
                    )
            ),
    ) {
        CompositionLocalProvider(LocalContentAlpha provides 1f) {
            ProvideTextStyle(
                value = MaterialTheme.typography.button
            ) {
                Row(
                    Modifier
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight
                        )
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}


@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    rippleRadius: Dp = 24.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .minimumTouchTargetSize()
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, radius = rippleRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        val contentAlpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha, content = content)
    }
}


@OptIn(
    ExperimentalMaterialApi::class
)
@Composable
fun MusicItem(
    music: Music,
    isMusicPlayed: Boolean,
    modifier: Modifier = Modifier,
    showImage: Boolean = true,
    showDuration: Boolean = true,
    showTrailingIcon: Boolean = false,
    trailingIcon: @Composable ColumnScope.() -> Unit = {},
    onClick: () -> Unit,
    enableDeleteAction: Boolean = false,
    deleteMusic: (music: Music) -> Unit
) {

    val delete = SwipeAction(
        onSwipe = {
            deleteMusic(music)
        },
        icon = {
            Icon(
                modifier = Modifier.padding(16.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete icon",
                tint = white
            )
        },
        background = sunset_orange
    )
    SwipeableActionsBox(
        swipeThreshold = 90.dp,
        endActions = if (enableDeleteAction) listOf(delete) else listOf()
    ) {
        Card(
            elevation = 0.dp,
            backgroundColor = if (isSystemInDarkTheme()) background_dark else background_light,
            shape = RectangleShape,
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
                    .background(if (isSystemInDarkTheme()) background_dark else background_light)
            ) {
                if (showImage) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = music.albumPath.toUri())
                                .apply(block = fun ImageRequest.Builder.() {
                                    error(R.drawable.ic_music_unknown)
                                    placeholder(R.drawable.ic_music_unknown)
                                }).build()
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .weight(0.18f, fill = false)
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 12.dp)
                        .weight(0.6f)
                ) {
                    Text(
                        text = music.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = typographyDmSans().body1.copy(
                            color = if (isMusicPlayed) sunset_orange else typographyDmSans().body1.color,
                            fontSize = TextUnit(14f, TextUnitType.Sp),
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        text = "${music.artist} • ${music.album}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = typographySkModernist().body1.copy(
                            color = if (isMusicPlayed) sunset_orange else typographySkModernist().body1.color.copy(
                                alpha = 0.7f
                            ),
                            fontSize = TextUnit(12f, TextUnitType.Sp),
                        ),
                        modifier = Modifier
                            .padding(top = if (showDuration) 4.dp else 6.dp)
                    )

                    if (showDuration) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .wrapContentSize(Alignment.BottomStart)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_clock),
                                tint = if (isMusicPlayed) sunset_orange else {
                                    if (isSystemInDarkTheme()) white.copy(alpha = 0.7f) else black.copy(
                                        alpha = 0.7f
                                    )
                                },
                                contentDescription = null,
                                modifier = Modifier
                                    .size(13.dp)
                            )

                            Text(
                                text = SimpleDateFormat(
                                    "mm:ss",
                                    Locale.getDefault()
                                ).format(music.duration),
                                style = typographySkModernist().body1.copy(
                                    color = if (isMusicPlayed) sunset_orange else typographySkModernist().body1.color.copy(
                                        alpha = 0.7f
                                    ),
                                    fontSize = TextUnit(12f, TextUnitType.Sp)
                                ),
                                modifier = Modifier
                                    .padding(start = 4.dp)
                            )
                        }
                    }
                }
                if (showTrailingIcon) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        content = trailingIcon,
                        modifier = Modifier
                            .size(48.dp)
                            .weight(0.08f, fill = false)
                    )
                }
            }
        }
    }
}


@OptIn(
    ExperimentalMaterialApi::class
)
@Composable
fun AlbumItem(
    musicList: List<Music>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val context = LocalContext.current

    val musicIndexForAlbumThumbnail by remember { mutableStateOf(0) }

    Card(
        elevation = 0.dp,
        backgroundColor = if (isSystemInDarkTheme()) background_dark else background_light,
        shape = RoundedCornerShape(14.dp),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
                .background(if (isSystemInDarkTheme()) background_dark else background_light)
        ) {

            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = run {
                        if (musicList.isNotEmpty()) {
                            musicList[musicIndexForAlbumThumbnail].albumPath.toUri()
                        } else ContextCompat.getDrawable(context, R.drawable.ic_music_unknown)!!
                    }).apply(block = fun ImageRequest.Builder.() {
                        error(R.drawable.ic_music_unknown)
                        placeholder(R.drawable.ic_music_unknown)
                    }).build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Column(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = musicList[0].album,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = typographyDmSans().body1.copy(
                        fontSize = TextUnit(14f, TextUnitType.Sp),
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = musicList[0].artist,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = typographySkModernist().body1.copy(
                        color = typographySkModernist().body1.color.copy(alpha = 0.7f),
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                    ),
                    modifier = Modifier
                        .padding(top = 6.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlaylistItem(
    playlist: Playlist,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val musicIndexForAlbumThumbnail by remember { mutableStateOf(0) }

    Card(
        elevation = 0.dp,
        backgroundColor = if (isSystemInDarkTheme()) background_dark else background_light,
        shape = RoundedCornerShape(14.dp),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
                .background(if (isSystemInDarkTheme()) background_dark else background_light)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(
                        data = playlist.defaultImage ?: if (playlist.musicList.isNotEmpty()) {
                            playlist.musicList[musicIndexForAlbumThumbnail].albumPath.toUri()
                        } else ContextCompat.getDrawable(context, R.drawable.ic_music_unknown)!!
                    ).apply(block = fun ImageRequest.Builder.() {
                        error(R.drawable.ic_music_unknown)
                        placeholder(R.drawable.ic_music_unknown)
                    }).build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Column(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = playlist.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = typographyDmSans().body1.copy(
                        fontSize = TextUnit(16f, TextUnitType.Sp),
                        fontWeight = FontWeight.SemiBold
                    )
                )
                playlist.musicList.size.let {
                    Text(
                        text = "$it " + if (it > 1) "songs" else "song",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = typographySkModernist().body1.copy(
                            color = typographySkModernist().body1.color.copy(alpha = 0.7f),
                            fontSize = TextUnit(14f, TextUnitType.Sp),
                        ),
                        modifier = Modifier
                            .padding(top = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                tint = if (isSystemInDarkTheme()) white.copy(alpha = 0.6f) else background_content_dark.copy(
                    alpha = 0.6f
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
            )
        }
    }
}

@Composable
fun SetTimerSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {

    val tickItems = listOf(
        "Non-active",
        "30 mnt",
        "60 mnt",
        "90 mnt"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {

        SliderDefaults.Tick.VerticalLines(
            items = tickItems,
            tickColor = if (isSystemInDarkTheme()) background_content_light else background_content_dark,
            style = typographySkModernist().body1.copy(
                fontSize = TextUnit(12f, TextUnitType.Sp),
                textAlign = TextAlign.Center
            )
        )

        Slider(
            thumbRadius = 6.dp,
            value = value,
            valueRange = 0f..90f,
            steps = 90,
            onValueChange = { newValue ->
                onValueChange(newValue)
            },
            colors = SliderDefaults.colors(
                activeTrackColor = sunset_orange,
                activeTickColor = Color.Transparent,
                inactiveTrackColor = if (isSystemInDarkTheme()) background_content_light else background_content_dark,
                inactiveTickColor = Color.Transparent,
                thumbColor = sunset_orange
            )
        )
    }
}

@Composable
fun PlaylistDropMenu(
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    androidx.compose.material.IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            tint = white
        )
        DropdownMenu(
            modifier = Modifier.background(backgroundColor),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            DropdownMenuItem(onClick = {
                expanded = false
                onEditClicked()
            }) {
                Row(Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Button",
                        tint = if (isSystemInDarkTheme()) white else black,
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = stringResource(id = R.string.edit),
                        modifier = Modifier.padding(start = 5.dp),
                        color = if (isSystemInDarkTheme()) white else black,
                    )
                }
            }
            DropdownMenuItem(onClick = {
                expanded = false
                onDeleteClicked()
            }) {
                Row(Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete button",
                        tint = if (isSystemInDarkTheme()) white else black
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = stringResource(id = R.string.delete),
                        modifier = Modifier.padding(start = 5.dp),
                        color = if (isSystemInDarkTheme()) white else black
                    )
                }
            }
        }
    }
}
