package tech.zemn.mobile

sealed class Screens {
    sealed class Home: Screens() {
        object AllSongs: Home()
        object Albums: Home()
        object Artists: Home()
    }
    object NowPlaying: Screens()
}
