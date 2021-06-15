package com.simpleapps.weather.ui.splash

import android.graphics.Color
import androidx.navigation.fragment.findNavController
import com.mikhaellopez.rxanimation.*
import com.simpleapps.weather.R
import com.simpleapps.weather.core.BaseFragment
import com.simpleapps.weather.core.Constants
import com.simpleapps.weather.databinding.FragmentSplashBinding
import com.simpleapps.weather.di.Injectable
import com.simpleapps.weather.utils.extensions.hide
import com.simpleapps.weather.utils.extensions.show
import io.reactivex.disposables.CompositeDisposable

class SplashFragment : BaseFragment<SplashFragmentViewModel, FragmentSplashBinding>(R.layout.fragment_splash, SplashFragmentViewModel::class.java), Injectable {

    var disposable = CompositeDisposable()

    override fun init() {
        super.init()
        if (binding.viewModel?.sharedPreferences?.getString(Constants.Coords.LON, "").isNullOrEmpty()) {
            binding.buttonExplore.show()
            binding.welcomeTv.show()
            binding.viewModel?.navigateDashboard = false
        } else {
            binding.buttonExplore.hide()
            binding.viewModel?.navigateDashboard = true
        }
        binding.viewModel?.navigateDashboard?.let { startSplashAnimation(it) }

        binding.buttonExplore.setOnClickListener {
            binding.viewModel?.navigateDashboard?.let { it1 -> endSplashAnimation(it1) }
        }

        binding.rootView.setOnClickListener {
            binding.viewModel?.navigateDashboard?.let { it1 -> endSplashAnimation(it1) }
        }
    }

    private fun startSplashAnimation(navigateToDashboard: Boolean) {
        val duration = 500L
        disposable.add(
                RxAnimation.sequentially(
                        RxAnimation.together(
                                binding.imageViewBottomDrawable.translationY(500f),
                                binding.imageViewEllipse.fadeOut(0L),
                                binding.imageViewBottomDrawable.fadeOut(0L),
                                binding.imageViewBigCloud.translationX(-500F, 0L),
                                binding.imageViewSmallCloud.translationX(500f, 0L),
                                binding.imageViewBottomDrawableShadow.translationY(500f),
                                binding.imageViewMainCloud.fadeOut(0L),
                                binding.buttonExplore.fadeOut(0L),
                                binding.welcomeTv.fadeOut(0L),
                                binding.imageViewBottomDrawableShadow.fadeOut(0L)
                        ),

                        RxAnimation.together(
                            binding.imageViewBottomDrawable.fadeIn(duration),
                            binding.imageViewBottomDrawable.translationY(-1f),
                            binding.imageViewBottomDrawableShadow.fadeIn(1250L),
                            binding.imageViewBottomDrawableShadow.translationY(-1f),
                            binding.imageViewEllipse.fadeIn(duration),
                            binding.imageViewEllipse.translationY(-50F, duration)
                        ),

                        RxAnimation.together(
                            binding.imageViewBigCloud.translationX(-15f, duration),
                            binding.imageViewSmallCloud.translationX(25f, duration),
                            binding.imageViewMainCloud.fadeIn(500L),
                            binding.buttonExplore.fadeIn(duration),
                            binding.welcomeTv.fadeIn(duration)
                        )
                ).doOnTerminate {
                    findNavController().graph.startDestination = R.id.dashboardFragment // Little bit tricky solution :)
                    if (navigateToDashboard)
                        endSplashAnimation(navigateToDashboard)
                }
                        .subscribe()
        )
    }

    private fun endSplashAnimation(navigateToDashboard: Boolean) {
        val duration = 200L
        disposable.add(
                RxAnimation.sequentially(
                        RxAnimation.together(
                            binding.imageViewBottomDrawable.fadeOut(duration),
                            binding.imageViewBottomDrawable.translationY(100f),
                            binding.imageViewBottomDrawableShadow.fadeOut(duration),
                            binding.imageViewBottomDrawableShadow.translationY(100f)
                        ),

                    RxAnimation.together(
                        binding.imageViewEllipse.fadeOut(duration),
                        binding.imageViewEllipse.translationY(500F, duration)
                    ),

                    RxAnimation.together(
                        binding.imageViewBigCloud.translationX(500f, duration),
                        binding.imageViewSmallCloud.translationX(-500f, duration)
                    ),

                    binding.imageViewMainCloud.fadeOut(duration),
                    binding.buttonExplore.fadeOut(duration),
                    binding.welcomeTv.fadeIn(duration),
                    binding.rootView.backgroundColor(
                        Color.parseColor("#5D50FE"),
                        Color.parseColor("#FFFFFF"),
                        duration = 750L
                    )
                )
                        .doOnTerminate {
                            findNavController().graph.startDestination = R.id.dashboardFragment // Little bit tricky solution :)
                            if (navigateToDashboard)
                                navigate(R.id.action_splashFragment_to_dashboardFragment)
                            else
                                navigate(R.id.action_splashFragment_to_searchFragment)
                        }
                        .subscribe()

        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
