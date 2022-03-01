Diese ReadMe gibt einen Überblick über die Dateien und Skripte in diesem Ordner.
Es solte möglich sein, die Diagramme in `plots/` (fast) vollautomatisch zu erzeugen.

#### In Kürze
Folgende Forgehensweise führt zu den fertigen Diagrammen:
1. Herunterladen der Projekte nach `jars/`
2. `performance-experiments.sh` ausführen (die Dateien in `reports/` wurden so erzeugt)
3. `plot-performance-experiments.py` ausführen (die Dateien in `plots/` wurden so erzeugt)

#### jars.txt
Enthält urls zu allen Projekten, die bei der Evaluation verwendet werden sollen.
Die folgenden Skripte verwenden alle *jars*, welche im Verzeichnis `jars/` liegen.  
Herunterladen aller *jars* in `jars.txt` nach `jars/` mit `bash/wget`:
```
mkdir -p jars/
for url in $(grep ^http jars.txt); do
  wget -qP jars/ "$url"
done
```
#### classes-methods-codesize.csv
Zu den *jars* aus `jars.txt` enthält diese Datei Informationen zur Projektgröße (codeSize, Anzahl Klassen und Methoden).
Die Zahlen wurden mit unserem Framework gewonnen. Allerdings sind diese Metriken nicht direkt mit dem Build vom *main* oder *develop* Branch verfügbar. Deswegen liefern wir diese Datei einfach fest mit.

#### performance-experiments.sh
Dieses Skript führt die eigentliche Performanceanalyse durch. Es werden alle *jars* in `jars/` verwendet (`--batch-mode jars/`).
Die Ergebnisse von 10 Durchläufen landen im Verzeichnis `reports/`.
Zum Ausführen wird eine bash (bash-kompatible) shell benötigt.

#### plot-performance-experiments.py
Das Python-Skript erzeugt die Diagramme und schreibt sie nach `plots/`.
Es werden die Informationen aus `classes-methods-codesize.csv` und `performance-report-*.csv` verwendet.
Außerdem werden noch die Dateigrößen der *jars* in `jars/` ermittelt.
Zum Ausführen benötigen wir `Python 3` und einige weitere Bibliotheken. Die Abhängigkeiten können bspw. mit `virtualenv` ins lokale Verzeichnis installiert werden.
```
$ virtualenv py-venv
$ ./py-venv/bin/pip install pandas matplotlib
```
Skript ausführen:
```
$ ./py-venv/bin/python plot-performance-experiments.py
```
