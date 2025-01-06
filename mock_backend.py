# https://pythonbasics.org/webserver/
# https://docs.python.org/3/library/http.server.html
# https://www.geeksforgeeks.org/building-a-basic-http-server-from-scratch-in-python/
from http.server import BaseHTTPRequestHandler, HTTPServer
from os import urandom
import time

num_posts = 100

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
			post_id = num_posts
	
	if int(post_id) > num_posts:
		post_id = num_posts
	if post_id == 97:
		post_id = 96
	if post_id == 86:
		post_id = 85
	
	if post_id < 0:
		self.wfile.write(bytes("{}" + "   ", "utf-8"))
	else:
		self.wfile.write(bytes(f'''{{
			"id": {post_id},
			"image_ref": "https://picsum.photos/id/{post_id}/480/320",
			"latitude": 56.161224,
			"longitude": 15.586900
		}}''' + "   ", "utf-8"))

def handleNewTokenQuery(self):
	self.send_response(200)
	self.send_header("content-type", "application/json")
	self.end_headers()
	self.wfile.write(bytes(f'''{{
		"token": {urandom(64).hex()}
	}}''' + "   ", "utf-8"))

def handleNewPostQuery(self):
	self.send_response(201)
	self.end_headers()
	
	coordinates = ""
	while True:
		byte = self.rfile.read(1)
		if byte == b'':
			raise "Unexpected EOF in makePost header"

		if byte == b'\0':
			break
		coordinates += byte.decode("ascii")
	
	a, b = coordinates.split(",")
	a = float(a)
	b = float(b)
	
	image_bytes = self.rfile.read()
	
	print("RECIEVED POST", a, b, "post.jpg", "by user: ", self.headers["Authorization"])
	out = open("post.jpg", "wb")
	out.write(image_bytes)
	out.close()

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
		elif self.path == "/new-post":
			handleNewPostQuery(self)
		else:
			self.send_response(404)


port = 4000
server = HTTPServer(("", port), Handler)
server.serve_forever()
