package com.ranicorp.heygongccamera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moveToFirstScreen()
    }

    private fun moveToFirstScreen() {

    }
}
//0.storage에 userInfo를 가져와서
//1. userInfo가 있으면 QR 화면으로
//2. userInfo가 없으면 Terms 동의 여부 확인
//3. 아직 동의 안 했을 경우 Tems 화면으로
//4. 동의 했을 경우 QR 화면으로