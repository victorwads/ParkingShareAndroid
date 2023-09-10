package br.com.victorwads.parkingshare.presentation.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.victorwads.parkingshare.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreenWithGoogle(onSuccess: () -> Unit) {
    var stateLogin by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

            FirebaseAuth
                .getInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) onSuccess()
                    else {
                        Toast.makeText(
                            context, "Google Sign-In failed!", Toast.LENGTH_SHORT
                        ).show()
                        stateLogin = false
                    }
                }
        } catch (e: ApiException) {
            stateLogin = false
            Toast.makeText(context, "Google Sign-In failed!", Toast.LENGTH_SHORT).show()
        }
    }
    val launcher = {
        stateLogin = true
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestId()
            .requestEmail()
            .requestProfile()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            onSuccess()
        }
        if (firstTime) {
            firstTime = false
            launcher()
        }
    }
    LoginScreen(launcher, stateLogin)
}

var firstTime = true

@Preview
@Composable
private fun LoginScreen(launch: () -> Unit = {}, loading: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp)
            )
        } else {
            Button(
                onClick = launch,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.TwoTone.Person,
                    contentDescription = "Google",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Login with Google")
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenLoading() {
    LoginScreen(loading = true)
}
