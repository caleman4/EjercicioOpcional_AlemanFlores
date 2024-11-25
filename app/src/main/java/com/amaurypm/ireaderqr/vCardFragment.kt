package com.amaurypm.ireaderqr

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.amaurypm.ireaderqr.databinding.FragmentScannerBinding
import com.amaurypm.ireaderqr.databinding.FragmentVCardBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class vCardFragment : Fragment() {
    private var _binding: FragmentVCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVCardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGenerar.setOnClickListener {
            val name = binding.etName.text.toString()
            val phone = binding.etPhone.text.toString()
            val email = binding.etEmail.text.toString()
            if (name.isNotEmpty() && phone.isNotEmpty()){

                val vCard = getString(R.string.vCard,name,phone,email).trimIndent()

                try {
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                        vCard,
                        BarcodeFormat.QR_CODE,
                        400, // Ancho
                        400  // Alto
                    )
                    val qrImageView: ImageView = binding.qrImageView
                    qrImageView.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }else{
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toastFalta),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


}