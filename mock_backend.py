# https://pythonbasics.org/webserver/
# https://docs.python.org/3/library/http.server.html
# https://www.geeksforgeeks.org/building-a-basic-http-server-from-scratch-in-python/
from http.server import BaseHTTPRequestHandler, HTTPServer
from os import urandom
import time

def handlePostQuery(self):
	self.send_response(200)
	
	self.send_header("content-type", "application/json")
	self.end_headers()
	
	match self.path.strip("/posts/").split("/"):
		case [f, p]:
			filter_id = f
			post_id = int(p)
		
		case [f]:
			filter_id = f
			post_id = 10
	
	if int(post_id) > 10:
		post_id = 10

	if post_id < 0:
		self.wfile.write(bytes("{}" + "   ", "utf-8"))
	else:
		self.wfile.write(bytes(f'''{{
			"id": {post_id},
			"image_ref": "https://picsum.photos/id/{post_id}/720/1280",
			"latitude": 56.15,
			"longitude": 15.58
		}}''' + "   ", "utf-8"))

def handleNewTokenQuery(self):
	self.send_response(200)
	self.send_header("content-type", "application/json")
	self.end_headers()
	self.wfile.write(bytes(f'''{{
		"token": {urandom(64).hex()}
	}}''' + "   ", "utf-8"))

class Handler(BaseHTTPRequestHandler):
	def do_GET(self):
		print(f'	Auth: {self.headers["Authorization"]}')
		if self.path.startswith("/posts"): 
			handlePostQuery(self)
		else:
			self.send_response(404)
	
	def do_POST(self):
		if self.path == "/new-token":
			handleNewTokenQuery(self)
		else:
			self.send_response(404)


port = 4000
server = HTTPServer(("", port), Handler)
server.serve_forever()
