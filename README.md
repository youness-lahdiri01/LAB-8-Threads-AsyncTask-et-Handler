# LAB 8 — Threads, AsyncTask et Handler

## Cours
Programmation Mobile : Android avec Java

---

# Aperçu

Ce travail pratique apprend à exécuter un traitement long (ex : calcul lourd ou chargement d’image) **sans bloquer l’interface utilisateur**.

L’application Android créée contient plusieurs boutons :

- un bouton pour lancer un traitement long
- un bouton pour afficher un Toast immédiatement

Si l’interface reste fluide pendant l’exécution du traitement, alors la **programmation asynchrone est correcte**.

---

# Vue d'ensemble

Dans Android, toutes les interactions avec l’interface graphique se font dans le **UI Thread (Main Thread)**.

Si une opération lourde est exécutée dans ce thread :

- l’application peut **se figer**
- Android peut afficher **Application Not Responding (ANR)**

La solution est d’exécuter ces tâches dans un **Worker Thread**.

Ce laboratoire montre plusieurs méthodes pour le faire :

- Thread + Runnable
- Handler
- AsyncTask

---

# Objectifs

Les objectifs de ce laboratoire sont :

- comprendre la différence entre **UI Thread et Worker Thread**
- exécuter des tâches longues **sans bloquer l’interface**
- mettre à jour l’UI depuis un thread secondaire
- utiliser **AsyncTask pour gérer un traitement long**
- observer le comportement de l’interface pendant l’exécution

---

# Concepts

### UI Thread

Le **UI Thread** est responsable de :

- dessiner l’interface
- gérer les événements utilisateur
- mettre à jour les composants visuels

Seul ce thread peut modifier les vues Android.

---

### Worker Thread

Un **Worker Thread** permet d’exécuter :

- calculs lourds
- accès réseau
- chargement d’images
- traitements longs

Cela évite de bloquer l’interface utilisateur.

---

### AsyncTask

AsyncTask simplifie l’exécution d’une tâche en arrière-plan.

Les principales méthodes sont :

```
onPreExecute()
doInBackground()
publishProgress()
onPostExecute()
```

---

# Étape 0 — Ce que l’application doit faire

L’application doit contenir :

- un **TextView** pour afficher l’état
- une **ProgressBar horizontale**
- un **ImageView**
- trois boutons :

1. Charger image (Thread)
2. Calcul lourd (AsyncTask)
3. Afficher Toast

---

# Étape 1 — Créer le projet

Créer un nouveau projet Android :

```
File → New Project → Empty Activity
```

Paramètres :

```
Nom : LabThreadsAsyncTask
Langage : Java
Minimum SDK : API 21
```

---

# Étape 2 — Créer l’interface (XML)

Dans `activity_main.xml`, ajouter :

- TextView
- ProgressBar
- ImageView
- trois boutons

Exemple :

```xml
<TextView
    android:id="@+id/txtStatus"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Statut : prêt"/>
```

```xml
<ProgressBar
    android:id="@+id/progressBar"
    style="?android:attr/progressBarStyleHorizontal"
    android:max="100"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

---

# Étape 3 — Préparer une image à charger

Ajouter une image dans :

```
res/drawable
```

Exemple :

```
image_android.png
```

Cette image sera chargée lors du clic sur le bouton.

---

# Étape 4 — Code Java étape par étape

## Charger image avec Thread

Lorsqu’on clique sur le bouton :

- un **Worker Thread** est lancé
- il simule un chargement avec `sleep()`
- l’interface est mise à jour avec `runOnUiThread()`

Exemple :

```java
new Thread(() -> {

    try {
        Thread.sleep(3000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    runOnUiThread(() -> {
        imageView.setImageResource(R.drawable.image_android);
        txtStatus.setText("Statut : image chargée (Thread)");
    });

}).start();
```

---

## Calcul lourd avec AsyncTask

AsyncTask permet d’exécuter un traitement long.

Structure :

```java
private class HeavyTask extends AsyncTask<Void, Integer, Integer> {

    protected void onPreExecute() {
        progressBar.setProgress(0);
    }

    protected Integer doInBackground(Void... voids) {

        for(int i=0;i<=100;i++){

            publishProgress(i);

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return 100;
    }

    protected void onProgressUpdate(Integer... values){
        progressBar.setProgress(values[0]);
    }

    protected void onPostExecute(Integer result){
        txtStatus.setText("Statut : calcul terminé résultat = "+result);
    }
}
```

---

# Étape 5 — Test (validation)

## Test 1 — Chargement avec Thread

Cliquer sur :

```
Charger image (Thread)
```

Résultat attendu :

```
Statut : image chargée (Thread)
```

Pendant ce temps, cliquer sur **Afficher Toast**.

Le Toast doit apparaître immédiatement.

Cela prouve que **l’interface n’est pas bloquée**.

---

## Test 2 — Calcul avec AsyncTask

Cliquer sur :

```
Calcul lourd (AsyncTask)
```

Résultat attendu :

- la **ProgressBar progresse de 0 à 100**
- le statut affiche :

```
Statut : calcul terminé résultat = 100
```

---

# Conclusion

Ce laboratoire montre comment :

- éviter de bloquer l’interface Android
- exécuter des tâches longues en arrière-plan
- mettre à jour correctement l’interface utilisateur

Les **Threads, AsyncTask et Handler** permettent de garder une application **fluide et réactive**.
