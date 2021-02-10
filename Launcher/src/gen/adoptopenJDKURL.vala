using Curl;
using Json;
namespace Launcher {
 class AdoptOpenJDK {
  public string? obtain() {
   string url = "https://api.adoptopenjdk.net/v3/assets/latest/15/hotspot";
   StringBuilder sb = new StringBuilder.sized(50000);
   var handle = new EasyHandle();
   if(handle != null) {
    handle.setopt(URL,url);
    handle.setopt(WRITEFUNCTION,appendJSON);
    handle.setopt(VERBOSE,true);
    handle.setopt(SSL_VERIFYPEER,false);
    handle.setopt(WRITEDATA,sb);
    Code c = handle.perform();
    if(c != OK){
     GLib.stderr.printf("%s\n",Global.strerror(c));
    }
   }
   return parseJSON(sb.str);
  }
  string? parseJSON(string json) {
   var parser = new Parser();
   parser.load_from_data(json);
   var node = parser.get_root();
   var array = node.get_array();
   for(var i = 0u; i < array.get_length(); i++) {
    var object = array.get_object_element(i);
    var binaryObject = object.get_object_member("binary");
    if(!binaryObject.has_member("package")) {
     continue;
    }
    var os = binaryObject.get_string_member("os");
    if(os != getOS()) {
     continue;
    }
    var arch = binaryObject.get_string_member("architecture");
    if(arch != getArch()) {
     continue;
    }
    var packageObject = binaryObject.get_object_member("package");
    return packageObject.get_string_member("link");
   }
   return null;
  }
  string getOS() {
   return "windows";
  }
  string getArch() {
   return "x64";
  }
  static size_t appendJSON(char* buffer, size_t size, size_t nitems, void* outstream) {
   ((StringBuilder)outstream).append_len((string)buffer,(ssize_t)(size*nitems));
   return size*nitems;
  }
 }
}
