package salir.musicplayer.domain.utils

fun millsToMinutesAndSeconds(mills: Int): String {
    val minutes = mills / 1000 / 60
    val seconds = mills / 1000 % 60
    return "%02d:%02d".format(minutes, seconds)
}