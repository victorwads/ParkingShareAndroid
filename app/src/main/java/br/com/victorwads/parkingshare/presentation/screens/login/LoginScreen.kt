package br.com.victorwads.parkingshare.presentation.screens.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.victorwads.parkingshare.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreenWithGoogle(onSuccess: () -> Unit) {
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
                    else Toast.makeText(
                        context, "Google Sign-In failed!", Toast.LENGTH_SHORT
                    ).show()
                }
        } catch (e: ApiException) {
            // Google Sign In failed, handle accordingly
            Toast.makeText(context, "Google Sign-In failed!", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            onSuccess()
        }
    }
    LoginScreen {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestId()
            .requestEmail()
            .requestProfile()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }
}

@Composable
fun LoginScreen(launch: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Login",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = launch,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Login with Google")
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}