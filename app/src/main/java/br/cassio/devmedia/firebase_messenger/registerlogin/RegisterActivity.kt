package br.cassio.devmedia.firebase_messenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import br.cassio.devmedia.firebase_messenger.messages.LatestMessagesActivity
import br.cassio.devmedia.firebase_messenger.R
import br.cassio.devmedia.firebase_messenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        /**
         * Botão cadastrar
         */
        register_button_register.setOnClickListener {

            perFormaRegister()

        }

        /**
         * Ja tem uma conta ?  ao clicar no botão vai abrir activity login
         */
        already_have_account_text_view.setOnClickListener {

            Log.d("MainActivity", "mostrar a atividade de login")

            /**
             * navegar entre as activity
             */
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        /**
         * Apresenta o seletor de fotos
         */
        selectphoto_select_button_register.setOnClickListener {
            Log.d("RegisterActivity", "mostrar o seletor de fotos")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }

    //Foto selelcionada
    var selectPhotoUri: Uri? = null

    /**
     * Função selecionar a foto
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(resultCode, requestCode, data)
        //continue e verifique qual foi a imagem selecionada
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            Log.d(TAG, "A foto foi selecionada")

            selectPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUri)
            select_image_view_register.setImageBitmap(bitmap)
            selectphoto_select_button_register.alpha = 0f
            //  val bitmapDrawable = BitmapDrawable(bitmap)
            // selectphoto_select_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    /**
     * Classe cadastrar e validar cadastro em firebase.
     * validacão de campos de cadastro
     */
    private fun perFormaRegister() {

        val name = name_edittext_register.text.toString()
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        /**
         * validação de campos de cadastro se estiverem em branco
         */
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Os campos são OBRIGATÓRIOS ", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Name: $name")
        Log.d(TAG, "E-mail is:" + email)
        Log.d(TAG, "Password: $password")

        /**
         * Autenticação FireBase para criar um usuário com email e senha
         */
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if bem-sucedida
                Log.d(
                    TAG,
                    "( Boa )User criado com sucesso ${it.result?.user?.uid}"
                )
                Toast.makeText(
                    this,
                    "Conta cadastrada: ${it.result?.user?.uid}",
                    Toast.LENGTH_SHORT
                ).show()

                updateFirebaseImageToStorage()
            }
            .addOnFailureListener {

                Log.d(TAG, "( FALHA )Falha ao criar user: ${it.message}")
                Toast.makeText(
                    this, "Erro ao criar user: ${it.message}", Toast
                        .LENGTH_SHORT
                ).show()
            }
    }

    /**
     * Upload de iamgem em firebase
     *
     */
    private fun updateFirebaseImageToStorage() {
        if (selectPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity","sucesso update imagem: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener { it ->

                    Log.d(TAG, "Localização de arquivo: $it")

                    saveUserToFirebaseDatabase(it.toString())

                }

            }
            .addOnFailureListener{
                Log.d(TAG, "Falha ao definir valor para database${it.message}")
            }

    }

    /**
     * Salve user imagem firebase database e inicializa a ActivityLatest
     * messages
     */
private fun saveUserToFirebaseDatabase(profileImageUrl: String){
       val uid = FirebaseAuth.getInstance().uid?:""
       val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

       val user = User(
           uid,
           name_edittext_register.text.toString(),
           profileImageUrl
       )
       ref.setValue(user)
           .addOnSuccessListener {
               Log.d(TAG, "Finalizando save user em firebase database")

               /**
                * apos salvar dados
                * iniciando LatestMessagesActivity
                */
               val intent = Intent(this, LatestMessagesActivity::class.java)
               intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
               startActivity(intent)

           }
           }

   }


