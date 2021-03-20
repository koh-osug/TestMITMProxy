import http.server as server
import socketserver

PORT = 8000

Handler = server.SimpleHTTPRequestHandler
Handler.extensions_map = {'.pem': 'application/x-x509-ca-cert', '.crt': 'application/x-x509-ca-cert', '.txt': 'text/plain', '': 'application/octet-stream'}
httpd = socketserver.TCPServer(("", PORT), Handler)
print("serving at port %d" % PORT)
httpd.serve_forever()