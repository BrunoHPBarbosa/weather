package com.example.weather

// LoadingAnimationUtil.kt
// LoadingAnimationUtil.kt
// LoadingAnimationUtil.kt
// LoadingAnimationUtil.kt
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView

object LoadingAnimationUtil {
    fun showLoadingAnimation(context: Context, parentLayout: ViewGroup, layoutResId: Int): LottieAnimationView {
        // Infla o layout da animação de carregamento
        val loadingLayout = LayoutInflater.from(context).inflate(layoutResId, parentLayout, false)
        parentLayout.addView(loadingLayout)

        // Obtém a referência para a animação de Lottie
        val loadingView = loadingLayout.findViewById<LottieAnimationView>(R.id.load22)

        // Torna a animação de Lottie visível
        loadingView.visibility = View.VISIBLE

        return loadingView
    }

    fun hideLoadingAnimation(loadingView: LottieAnimationView) {
        // Torna a animação de Lottie invisível
        loadingView.visibility = View.GONE

        // Remove a animação de Lottie do layout pai (opcional, dependendo do seu caso de uso)
        val parent = loadingView.parent as? ViewGroup
        parent?.removeView(loadingView)
    }
}

