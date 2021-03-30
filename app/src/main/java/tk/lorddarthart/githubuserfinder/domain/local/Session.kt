package tk.lorddarthart.githubuserfinder.domain.local

import android.net.Uri

data class Session(
  var personName: String? = null,
  var personGivenName: String? = null,
  var personFamilyName: String? = null,
  var personEmail: String? = null,
  var personId: String? = null,
  var personPhoto: Uri? = null
)