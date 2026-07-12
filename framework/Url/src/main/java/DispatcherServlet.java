import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import annotation.Controller;
import annotation.UrlMapping;
import annotation.Mapping;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Déclaration de la Map globale
    private Map<String, Mapping> urlMappingMap = new HashMap<>();

    @Override
    public void init() throws ServletException {
        System.out.println("=== DEBUT SCANNING DES URLS ===");
        urlMappingMap.clear();
        
        List<Class<?>> annotatedClasses = scanControllers("controllers");
        
        for (Class<?> clazz : annotatedClasses) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(UrlMapping.class)) {
                    UrlMapping annotation = method.getAnnotation(UrlMapping.class);
                    String url = annotation.value();
                    
                    // Stockage du nom de la classe et du nom de l'annotation
                    Mapping mapping = new Mapping(clazz.getName(), UrlMapping.class.getName());
                    urlMappingMap.put(url, mapping);
                }
            }
        }
        System.out.println("=== FIN SCANNING DES URLS ===");
    }

    private List<Class<?>> scanControllers(String packageName) {
        List<Class<?>> controllers = new ArrayList<>();
        String path = packageName.replace('.', '/');
        try {
            ClassLoader classLoader = DispatcherServlet.class.getClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
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
                } else if (resource.getProtocol().equals("jar")) {
                    JarURLConnection jarConnection = (JarURLConnection) resource.openConnection();
                    try (JarFile jar = jarConnection.getJarFile()) {
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.startsWith(path + "/") && name.endsWith(".class")) {
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
            e.printStackTrace();
        }
        return controllers;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
        
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        request.setAttribute("listeMappings", urlMappingMap);
        request.getRequestDispatcher("/WEB-INF/vues/accueil.jsp").forward(request, response);
    }
}