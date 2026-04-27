import cherrypy
import os
import sys

# Ajouter le package train_data_service dans sys.path
package_path = os.path.join(os.getcwd(), "train_data_service")
sys.path.insert(0, package_path)

import start_service

# Force CherryPy on host 0.0.0.0
cherrypy.config.update({"server.socket_host": "0.0.0.0", "server.socket_port": 8081})

# Run service
start_service.main(sys.argv[1:])
