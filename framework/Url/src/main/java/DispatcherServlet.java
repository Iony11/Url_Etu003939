import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.JarURLConnection;
import java.net.URL;
import annotation.Controller;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        System.out.println("=== DEBUT SCANNING DES CONTROLLERS ===");
        
        String packageToScan = "controllers";
        List<Class<?>> annotatedClasses = scanControllers(packageToScan);
        
        // Affichage de la liste finale
        for (Class<?> clazz : annotatedClasses) {
            System.out.println("Contrôleur détecté : " + clazz.getName());
        }
        
        System.out.println("=== FIN SCANNING DES CONTROLLERS ===");
    }

    private List<Class<?>> scanControllers(String packageName) {
        List<Class<?>> controllers = new ArrayList<>();
        String path = packageName.replace('.', '/');
        
        try {
            ClassLoader classLoader = DispatcherServlet.class.getClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);
            
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                
                // Cas 1 : Les fichiers sont dans un dossier physique classique
                if (resource.getProtocol().equals("file")) {
                    File directory = new File(resource.getFile());
                    if (directory.exists() && directory.isDirectory()) {
                        File[] files = directory.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                if (file.getName().endsWith(".class")) {
                                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                                    Class<?> clazz = Class.forName(className);
                                    if (clazz.isAnnotationPresent(Controller.class)) {
                                        controllers.add(clazz);
                                    }
                                }
                            }
                        }
                    }
                } 
                // Cas 2 : Les fichiers sont empaquetés dans votre fichier .jar (Scénario Tomcat)
                else if (resource.getProtocol().equals("jar")) {
                    JarURLConnection jarConnection = (JarURLConnection) resource.openConnection();
                    try (JarFile jar = jarConnection.getJarFile()) {
                        Enumeration<JarEntry> entries = jar.entries();
                        
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            
                            // On cherche les fichiers .class dans le package spécifié
                            if (name.startsWith(path + "/") && name.endsWith(".class")) {
                                // Convertir le chemin du fichier en nom de classe (ex: controllers/BController.class -> controllers.BController)
                                String className = name.substring(0, name.length() - 6).replace('/', '.');
                                Class<?> clazz = Class.forName(className);
                                if (clazz.isAnnotationPresent(Controller.class)) {
                                    controllers.add(clazz);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du scan du package : " + e.getMessage());
            e.printStackTrace();
        }
        return controllers;
    }
    //qui capture les requqetes classiques (refa mi-click lien)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }
        
    //on renvoie la requête à la méthode processRequest pour traitement
    private void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Class<?>> annotatedClasses = scanControllers("controllers");
    
        // 2. On stocke cette liste dans un attribut de la requête nommé "listeControllers"
        request.setAttribute("listeControllers", annotatedClasses);
        
        // 3. On redirige vers la vue JSP
        request.getRequestDispatcher("/WEB-INF/vues/accueil.jsp").forward(request, response);
    }
}