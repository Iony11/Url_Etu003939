<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Accueil - Mon Framework de Test</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f7f6;
            color: #333;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .container {
            background-color: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            text-align: center;
            max-width: 500px;
        }
        h1 {
            color: #2c3e50;
            margin-bottom: 20px;
        }
        p {
            color: #7f8c8d;
            font-size: 1.1em;
            line-height: 1.6;
        }
        .badge {
            display: inline-block;
            background-color: #2ecc71;
            color: white;
            padding: 5px 10px;
            border-radius: 4px;
            font-weight: bold;
            margin-top: 15px;
        }
    </style>
</head>
<body>

    <div class="container">
    <h3>Liste des contrôleurs et annotations détectés :</h3>
    <ul style="text-align: left; display: inline-block; background: #f9f9f9; padding: 15px 30px; border-radius: 5px; border: 1px solid #ddd; list-style-type: disc;">
        <% 
            java.util.Map<String, annotation.Mapping> mappings = (java.util.Map<String, annotation.Mapping>) request.getAttribute("listeMappings");
            
            if (mappings != null && !mappings.isEmpty()) {
                for (java.util.Map.Entry<String, annotation.Mapping> entry : mappings.entrySet()) {
                    String url = entry.getKey();
                    annotation.Mapping m = entry.getValue();
        %>
                    <li style="margin: 8px 0; color: #2c3e50; font-family: monospace; font-size: 1.1em;">
                        <span style="color: #e74c3c; font-weight: bold;"> /<%= url %></span> &rarr; 
                        <strong><%= m.getClassName() %></strong> &rarr; 
                        <span style="color: #16a085;"><%= m.getMethod() %></span>
                    </li>
        <% 
                }
            } else {
        %>
                <li style="color: #e74c3c; font-family: monospace;">Aucun élément détecté.</li>
        <% 
            }
        %>
    </ul>
</div>

</body>
</html>