import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    //qui capture les requqetes classiques (refa mi-click lien)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    //qui capture les requêtes de soumission (formulaire)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }
    //on renvoie la requête à la méthode processRequest pour traitement
    private void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // On prépare le message
        request.setAttribute("message", "Bienvenue sur notre framework de test !");
        
        // Qu'importe l'URL tapée, on fait TOUJOURS le forward vers accueil.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/vues/accueil.jsp");
        dispatcher.forward(request, response);
    }
}