package com.amaurypm.ireaderqr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.amaurypm.ireaderqr.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var cameraPermissionGranted = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if(isGranted){
            //Tenemos el permiso
            actionPermissionGranted()
        }else{
            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                //Le puedo dar razones adicionales
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.permisoRequerido))
                    .setMessage(getString(R.string.permiso))
                    .setPositiveButton(getString(R.string.darPermiso)){ _, _ ->
                        updateOrRequestPermission()
                    }
                    .setNegativeButton(getString(R.string.salir)){ dialog, _ ->
                        dialog.dismiss()
                        requireActivity().finish()
                    }
                    .create()
                    .show()

            }else{
                //Me negó el permiso permanentemente
                Toast.makeText(
                    requireContext(),
                    getString(R.string.negadoPermanente),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.ivCamara.setOnClickListener {
            updateOrRequestPermission()
        }
        binding.ivContact.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_vCardFragment)
        }
    }

    private fun updateOrRequestPermission(){

        //Revisamos si tenemos el permiso o no
        cameraPermissionGranted =
            ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED

        if(!cameraPermissionGranted){
            //Pedimos el permiso
            permissionLauncher.launch(Manifest.permission.CAMERA)

        }else{
            //Tenemos el permiso
            actionPermissionGranted()
        }

    }

    private fun actionPermissionGranted(){
        findNavController().navigate(R.id.action_mainFragment_to_scannerFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}