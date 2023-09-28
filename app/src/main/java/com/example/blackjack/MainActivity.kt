package com.example.blackjack

import android.app.ActionBar.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.blackjack.model.Carta
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var random: Random
    private lateinit var llcartas: LinearLayout
    private val mazo = Array(40) {  Carta() }
    private var imazo = 0
    private val palos = arrayOf("clubs", "diamonds", "hearts", "spades")
    private var jugador = 0
    private var puntos = Array(2) {0}
    private var titulo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        random = Random(System.currentTimeMillis())

        titulo = title.toString()

        llcartas = findViewById<LinearLayout>(R.id.llcartas)
        val ivmazo = findViewById<ImageView>(R.id.ivmazo)
        val btnstop = findViewById<Button>(R.id.btnstop)

        ivmazo.setOnClickListener{ ivmazoOnCick() }
        btnstop.setOnClickListener { btnstopOnClick() }

        makeMazo()

        newGame()
    }

    private fun makeMazo() {
        var c = 0
        for (p in 0 until 4){
            for (n in 1..10){
                mazo[c] = Carta(n,p)
                c++
            }
        }
    }

    private fun newGame() {
        imazo = 0
        jugador = 0
        puntos = Array(2) {0}
        mazo.shuffle(random)
        llcartas.removeAllViews()
        sacaCarta()
        sacaCarta()
    }

    private fun sacaCarta(){
        val cartaJuego: Carta = mazo[imazo]

        imazo++
        if(imazo == 40) imazo = 0

        val ivcarta = ImageView(this)

        val idimage = resources.getIdentifier("${palos[cartaJuego.palo]}${cartaJuego.numero}", "drawable" , packageName)
        ivcarta.setImageResource(idimage)

        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.setMargins(8, 0, 8, 0)
        ivcarta.layoutParams = lp

        llcartas.addView(ivcarta, 0)

        val n = if (cartaJuego.numero>7) 10 else cartaJuego.numero
        puntos[jugador] += n

        title ="${titulo} - ${puntos[jugador]} puntos"

        if(puntos[jugador]>21 && jugador == 0) {
            tostada("Te pasaste Jugador 1")
            nextPlayer()
        } else if(jugador == 1 && puntos[jugador] > 21) {
            tostada("Te pasaste jugador 2")
            lose()
        }
    }

    private fun ivmazoOnCick() {
        sacaCarta()
    }

    private fun btnstopOnClick() {
        if(puntos[jugador]<21 && jugador == 0) {
            nextPlayer()
        } else {
            lose()
        }
    }

    private fun nextPlayer() {
        llcartas.removeAllViews()
        title  = "${titulo}"
        var nextPlayerDialog = AlertDialog.Builder(this)
        nextPlayerDialog.setMessage("Siguiente")
        nextPlayerDialog.setPositiveButton("Jugar") { _ , _ ->
            jugador = 1
            sacaCarta()
            sacaCarta()
        }
        nextPlayerDialog.setCancelable(false)
        nextPlayerDialog.show()
    }

    private fun lose() {
        var msg = ""
        if (puntos[0] > 21 && puntos[1] > 21) {
            msg = "Empate"
        } else if (puntos[0] > 21) {
            msg = "Gana Jugador 2"
        } else if(puntos[1]>21) {
            msg = "Gana Jugador 1"
        } else if ( 21-puntos[0] < 21-puntos[1] ){
            msg = "Gana Jugador 1"
        } else if (21-puntos[0] > 21-puntos[1]){
            msg = "Gana Jugador 2"
        } else {
            msg = "Empate"
        }

        var ganadorDialog = AlertDialog.Builder(this)
        ganadorDialog.setTitle(msg)
        ganadorDialog.setMessage("Jugador 1 : ${puntos[0]} puntos\n\nJugador 2 : ${puntos[1]} puntos")
        ganadorDialog.setPositiveButton("Nueva Partida") { _ , _ ->
            newGame()
        }
        ganadorDialog.setNegativeButton("Salir") { _ , _ ->
            finish()
        }
        ganadorDialog.setCancelable(false)
        ganadorDialog.show()
    }

    private fun tostada(msg:String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}