package controllers;

import annotation.Controller;
import annotation.UrlMapping;

@Controller
public class BController {

    @UrlMapping("afficher-b") // 🎯 L'annotation doit être ICI
    public void afficherB() {
        System.out.println("Méthode afficherB exécutée.");
    }

    @UrlMapping("accueil") // 🎯 Et ICI
    public void afficherAccueil() {
        System.out.println("Méthode afficherAccueil exécutée.");
    }
}