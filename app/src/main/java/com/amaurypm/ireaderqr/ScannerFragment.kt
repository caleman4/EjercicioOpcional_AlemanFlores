package com.amaurypm.ireaderqr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.amaurypm.ireaderqr.databinding.FragmentScannerBinding
import java.net.MalformedURLException
import java.net.URL


class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScannerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cbvScanner.decodeContinuous { result ->
            findNavController().navigate(R.id.action_scannerFragment_to_mainFragment)
            binding.cbvScanner.pause()
            try{
                val scannedText = result.text
                if (scannedText != null && scannedText.startsWith(getString(R.string.beginvcard))) {
                    handleVCard(scannedText)
                } else {
                    URL(result.text)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(result.text)
                    startActivity(intent)
                }
            }catch(e: MalformedURLException){
                //Manejamos el error
                Toast.makeText(
                    requireContext(),
                    getString(R.string.novalido),
                    Toast.LENGTH_LONG
                ).show()
            }



        }
    }

    private fun handleVCard(vCard: String) {
        Log.d("VCARD", vCard)
        val vcard2= "BEGIN:VCARD\n" +
                "VERSION:3.0 \n" +
                "FN:Amaury Héctor Perea Matsumura\n " +
                "TEL:5511223344 \n" +
                "EMAIL:amaury@comunidad.unam.mx \n" +
                "END:VCARD"
        // Extraer datos de la vCard manualmente (o usar una librería si es complejo)
        val name = Regex("FN:(.*)").find(vCard)?.groups?.get(1)?.value ?: ""
        //val phone = Regex("TEL.*:(.*)").find(vCard)?.groups?.get(1)?.value ?: ""
        val phones = parseMultiplePhones(vCard)
        val email = Regex("EMAIL.*:(.*)").find(vCard)?.groups?.get(1)?.value ?: ""

        // Crear un intent para abrir la aplicación de contactos
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME, name)
            //putExtra(ContactsContract.Intents.Insert.PHONE, phone)
            putExtra(ContactsContract.Intents.Insert.EMAIL, email)
            // Agregar el primer teléfono (principal)
            if (phones.isNotEmpty()) {
                putExtra(ContactsContract.Intents.Insert.PHONE, phones[0])
            }

            // Agregar teléfonos adicionales
            for ((index, phone) in phones.withIndex()) {
                if (index > 0) { // Evitar el primero, ya se agregó como principal
                    putExtra(
                        ContactsContract.Intents.Insert.DATA,
                        arrayOf(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            phone
                        )
                    )
                }
            }
        }

        // Lanzar la actividad de la aplicación de contactos
        startActivity(intent)
    }
    fun parseMultiplePhones(vCard: String): List<String> {
        // Regex para encontrar todos los teléfonos en la vCard
        val phoneRegex = Regex("TEL.*:(.*)")
        return phoneRegex.findAll(vCard)
            .map { it.groups[1]?.value ?: "" }
            .filter { it.isNotEmpty() }
            .toList()
    }

    override fun onPause() {
        super.onPause()
        binding.cbvScanner.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.cbvScanner.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}