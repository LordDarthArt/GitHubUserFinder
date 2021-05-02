package tk.lorddarthart.githubuserfinder.view.auth.additional

sealed class SignInVariants {
    object None: SignInVariants()
    object Google: SignInVariants()
    object Facebook: SignInVariants()
    object GitHub: SignInVariants()
}