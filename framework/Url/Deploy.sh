#!/bin/bash

echo "========================================"
echo "  Deploiement avec Generation de JAR"
echo "========================================"
echo

# ====================================================================
# CONFIGURATION CORRIGÉE POUR VOTRE STRUCTURE
# ====================================================================
APP_NAME="Url"
SRC_DIR="src/main/java"      
WEB_DIR="webapp/WEB-INF"     # 👈 Modifié pour pointer sur webapp/WEB-INF
LIB_DIR="webapp/WEB-INF/lib"      
BUILD_DIR="build"
JAR_NAME="mon-framework.jar"

# Détection automatique de TOMCAT_HOME
if [ -z "$TOMCAT_HOME" ]; then
    if [ -d "/opt/tomcatProject" ]; then
        TOMCAT_HOME="/opt/tomcatProject"
    elif [ -d "/home/ionyjohnson/Documents/programmation/tomcat/apache-tomcat-10.0.16" ]; then
        TOMCAT_HOME="/home/ionyjohnson/Documents/programmation/tomcat/apache-tomcat-10.0.16"
    elif [ -d "/usr/share/tomcat9" ]; then
        TOMCAT_HOME="/usr/share/tomcat9"
    elif [ -d "/usr/local/tomcat" ]; then
        TOMCAT_HOME="/usr/local/tomcat"
    else
        echo "ERREUR: TOMCAT_HOME introuvable"
        exit 1
    fi
fi

TOMCAT_WEBAPPS="$TOMCAT_HOME/webapps"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"

# Verification de l'existence de Tomcat
if [ ! -d "$TOMCAT_HOME" ]; then
    echo "ERREUR: TOMCAT_HOME introuvable: $TOMCAT_HOME"
    exit 1
fi

echo "Utilisation de TOMCAT_HOME: $TOMCAT_HOME"
echo

# ====================================================================
# [1/5] NETTOYAGE ET ARRET DE TOMCAT
# ====================================================================
echo "[1/5] Arret de Tomcat et nettoyage..."

if [ -f "$TOMCAT_HOME/bin/shutdown.sh" ]; then
    "$TOMCAT_HOME/bin/shutdown.sh" 2>/dev/null
    echo "Attente de l'arret de Tomcat (8 secondes)..."
    sleep 8
fi

rm -rf "$BUILD_DIR"
rm -f "$APP_NAME.war"
rm -f "$JAR_NAME"
rm -rf "$TOMCAT_WEBAPPS/$APP_NAME"
rm -rf "$TOMCAT_WEBAPPS/$APP_NAME.war"
rm -rf "$TOMCAT_HOME/work/Catalina/localhost/$APP_NAME"

echo "OK"
echo

# ====================================================================
# [2/5] COMPILATION ET CREATION DU JAR
# ====================================================================
# ====================================================================
# [2/5] COMPILATION ET CREATION DU JAR
# ====================================================================
echo "[2/5] Compilation et creation du fichier .jar..."

mkdir -p "bin_classes"

if [ ! -d "$SRC_DIR" ]; then
    echo "ERREUR: Le dossier source '$SRC_DIR' est introuvable !"
    rm -rf bin_classes
    exit 1
fi

# 1. On trouve TOUS les fichiers .java de manière récursive
find "$SRC_DIR" -name "*.java" > sources.txt

# 2. Compilation globale vers bin_classes
javac -encoding UTF-8 -cp "$SERVLET_API_JAR:$LIB_DIR/*" -d "bin_classes" @sources.txt

if [ $? -ne 0 ]; then
    echo "ERREUR: La compilation a echoue!"
    rm -f sources.txt
    rm -rf bin_classes
    exit 1
fi
rm -f sources.txt

# 3. CREATION DU JAR : On s'assure d'inclure toute l'arborescence compilée
jar -cvf "$JAR_NAME" -C bin_classes . > /dev/null

if [ ! -f "$JAR_NAME" ]; then
    echo "ERREUR: La creation du fichier JAR a echoue!"
    rm -rf bin_classes
    exit 1
fi

echo "Fichier JAR cree avec succes: $JAR_NAME"
rm -rf bin_classes 
echo "OK"
echo

# ====================================================================
# [3/5] PREPARATION DU DOSSIER DE DEPLOIEMENT (BUILD)
# ====================================================================
echo "[3/5] Preparation de la structure Web..."

# On recrée l'arborescence WEB-INF directement dans le build
mkdir -p "$BUILD_DIR/WEB-INF/lib"

# CORRECTION DU DOUBLON : On copie le CONTENU de WEB-INF dans build/WEB-INF/
if [ -d "$WEB_DIR" ]; then
    cp -r "$WEB_DIR"/* "$BUILD_DIR/WEB-INF/"
fi

# On injecte votre JAR fraîchement créé au bon endroit
cp "$JAR_NAME" "$BUILD_DIR/WEB-INF/lib/"

echo "OK"
echo

# ====================================================================
# [4/5] COPIE DES AUTRES LIBRAIRIES EXTERNES
# ====================================================================
echo "[4/5] Copie des librairies externes..."

for jar in "$LIB_DIR"/*.jar; do
    if [ -f "$jar" ] && [[ ! "$jar" =~ servlet-api ]]; then
        cp "$jar" "$BUILD_DIR/WEB-INF/lib/"
    fi
done

echo "OK"
echo

# ====================================================================
# [5/5] CREATION DU WAR ET DEPLOIEMENT GLOBAL
# ====================================================================
echo "[5/5] Creation du fichier WAR final et deploiement..."

cd "$BUILD_DIR" || exit 1
jar -cvf "../$APP_NAME.war" * > /dev/null 2>&1
cd ..

if [ ! -f "$APP_NAME.war" ]; then
    echo "ERREUR: Echec de la creation du fichier WAR"
    exit 1
fi

cp -f "$APP_NAME.war" "$TOMCAT_WEBAPPS/"
if [ -f "$TOMCAT_HOME/bin/startup.sh" ]; then
    "$TOMCAT_HOME/bin/startup.sh" > /dev/null 2>&1
    echo "Tomcat redemarre"
fi

echo "OK"
echo
echo "========================================"
echo "  Deploiement termine avec succes!"
echo "========================================"
echo "Votre fichier JAR a ete inclus dans l'application."
echo "Application disponible sur: http://localhost:8080/$APP_NAME/"
echo