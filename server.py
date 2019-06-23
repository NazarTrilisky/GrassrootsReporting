import os
import re
import logging
import base64
import json
import time

from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import unquote


def get_data_fields(in_str):
    """
    Arg: in_str: binary string data e.g. b'string here'
    Return a dict with keys as field names and values as strings
    """
    DELIMITER = b'Content-Disposition: form-data;'
    result = {}
    parts = in_str.split(DELIMITER)
    assert(len(parts) > 3)
    for part_bin in parts:
        part_str = part_bin.decode('utf-8', 'ignore')
        if len(part_str.strip()) < 20 or 'Content-Length:' not in part_str:
            continue
        try:
            key = re.search('name="(\w*)"\r\n', part_str).group(1).strip()
            start = re.search('Content-Length: \d+\r\n\r\n', part_str).end()
            end = re.search('\r\n\-\-[0-9a-zA-Z\-_]{3,}', part_str[start:]).start() + start
            val = part_str[start:end].strip()
            result[key] = val
        except Exception as exc:
            logging.error(exc)
    return result


class MyServer(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_GET(self):
        self._set_headers()
        print("Got GET request...")
        self.wfile.write("<html><body><h1>hi!</h1></body></html>")

    def do_HEAD(self):
        self._set_headers()
        
    def do_POST(self):
        content_length = int(self.headers['Content-Length']) # <--- Gets the size of data
        post_bytes = self.rfile.read(content_length) # <--- Gets the data itself
        fields = get_data_fields(post_bytes)
        for key in fields.keys():
            print(key + " --> " + unquote(fields[key][0:50]))
        savePostData(fields)
        self._set_headers()
        self.wfile.write(b'<html><body><h1>POST!</h1></body></html>')
        

def savePostData(fields):
    REPORT_BASE_NAME = str(int(time.time())) + "_"
    REPORTS_FOLDER = "Posted_Reports"
    SAVE_PATH = os.path.join(os.getcwd(), REPORTS_FOLDER)
    details_dict = {}  # dictionary without pictures
    for key in fields.keys():
        if re.search('photo_\d+' , key):
            imgdata = base64.b64decode(fields[key])
            filename = REPORT_BASE_NAME + key + '.png'
            filepath = os.path.join(SAVE_PATH, filename)
            with open(filepath, 'wb') as fh:
                fh.write(imgdata)
        else:
            details_dict[key] = fields[key]
    # save all non-image data in JSON format
    file_name = REPORT_BASE_NAME + "details.json"
    file_path = os.path.join(SAVE_PATH, file_name)
    with open(file_path, 'w') as fh:
        fh.write(json.dumps(details_dict))
        
        
def run(server_class=HTTPServer, handler_class=MyServer, port=8594):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print('Starting httpd...')
    httpd.serve_forever()

if __name__ == "__main__":
    from sys import argv

    if len(argv) == 2:
        run(port=int(argv[1]))
    else:
        run()
