# définir le host à 0.0.0.0 pour Docker
import cherrypy
import os
import sys

import booking_reference_service

cherrypy.config.update({"server.socket_host": "0.0.0.0"})

# passer les arguments au script original
booking_reference_service.main(sys.argv[1:])
