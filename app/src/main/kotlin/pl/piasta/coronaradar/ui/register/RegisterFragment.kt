package pl.piasta.coronaradar.ui.register

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.piasta.coronaradar.R
import pl.piasta.coronaradar.databinding.FragmentRegisterBinding
import pl.piasta.coronaradar.ui.base.BaseFragment

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, RegisterViewModel>(R.layout.fragment_register) {

    override val viewModel: RegisterViewModel by viewModels()
}