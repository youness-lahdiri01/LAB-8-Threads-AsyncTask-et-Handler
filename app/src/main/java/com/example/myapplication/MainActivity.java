package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 1) Références vers l’interface
    private TextView txtStatus;
    private ProgressBar progressBar;
    private ImageView img;

    // 2) Handler lié au UI thread (Main thread)
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // A) Lier les vues XML au code Java (findViewById)
        txtStatus = findViewById(R.id.txtStatus);
        progressBar = findViewById(R.id.progressBar);
        img = findViewById(R.id.img);

        Button btnLoadThread = findViewById(R.id.btnLoadThread);
        Button btnCalcAsync = findViewById(R.id.btnCalcAsync);
        Button btnToast = findViewById(R.id.btnToast);

        // B) Créer le Handler qui poste sur le UI thread
        mainHandler = new Handler(Looper.getMainLooper());

        // C) Bouton Toast : doit toujours répondre immédiatement
        btnToast.setOnClickListener(v ->
                Toast.makeText(getApplicationContext(), "UI réactive", Toast.LENGTH_SHORT).show()
        );

        // D) Lancer un Thread pour charger une image sans bloquer
        btnLoadThread.setOnClickListener(v -> loadImageWithThread());

        // E) Lancer un calcul lourd avec AsyncTask
        btnCalcAsync.setOnClickListener(v -> new HeavyCalcTask().execute());
    }

    // -----------------------------------------
    // PARTIE 1 : THREAD (niveau débutant)
    // -----------------------------------------
    private void loadImageWithThread() {

        // 1) Afficher la ProgressBar (UI thread)
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        txtStatus.setText("Statut : chargement image (Thread)...");

        // 2) Créer un thread de fond
        new Thread(() -> {

            // 3) Simuler un travail long (ex: téléchargement)
            try {
                Thread.sleep(1000); // 1 seconde
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 4) Charger l'image en background
            // Ici on utilise l'icône du projet (mipmap)
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            // 5) PROBLÈME : on ne peut pas modifier l'UI ici !
            // Solution : revenir au UI thread avec Handler.post(...)
            mainHandler.post(() -> {
                img.setImageBitmap(bitmap);
                progressBar.setVisibility(View.INVISIBLE);
                txtStatus.setText("Statut : image chargée (Thread)");
            });

        }).start(); // 6) start() démarre réellement le thread
    }

    // -----------------------------------------
    // PARTIE 2 : ASYNCTASK (pédagogique)
    // -----------------------------------------
    private class HeavyCalcTask extends AsyncTask<Void, Integer, Long> {

        // 1) Avant le traitement : UI thread
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            txtStatus.setText("Statut : calcul lourd (AsyncTask)...");
        }

        // 2) Traitement long : Worker thread
        @Override
        protected Long doInBackground(Void... voids) {
            long result = 0;

            for (int i = 1; i <= 100; i++) {

                // Simulation d'un calcul lourd
                for (int k = 0; k < 200000; k++) {
                    result += (i * k) % 7;
                }

                // Mise à jour progression
                publishProgress(i); // Appelle onProgressUpdate sur UI thread
            }

            return result;
        }

        // 3) Pendant la progression : UI thread
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        // 4) Après le traitement : UI thread
        @Override
        protected void onPostExecute(Long result) {
            progressBar.setVisibility(View.INVISIBLE);
            txtStatus.setText("Statut : calcul terminé résultat = " + result);
        }
    }
}