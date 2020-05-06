package br.cassio.devmedia.firebase_messenger.registerlogin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.cassio.devmedia.firebase_messenger.R
import com.google.firebase.auth.FirebaseAuth

//biblioteca synthetic.main.activity_login.* acessar activity layout
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        /**
         * Executa o código no Click (Exibir) depois que o usuário pressiona o botão.
         */
        login_button_login.setOnClickListener {

            /**
             * acessar seus campos de texto
             */
            val email = email_edit_login.text.toString()
            val password = password_edit_login.text.toString()

            /**
             * Registre uma mensagem de depuração dentro do logcat
             */
            Log.d("login", "Email and Password: $email / *******")

            /**
             * firebase auth e obtenha a instância, em vez de chamar create user
             * você só quer dizer entre com e-mail e senha
             */
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            // .addOnCompleteListener()
            //  .add
        }


        /**
         * terminaremos esta atividade e isso apenas sairá da atividade de login, retornando à atividade principal
         */
        back_to_register_textview.setOnClickListener {
            finish()
            Log.d("login", "Fim login")
        }


    }


}