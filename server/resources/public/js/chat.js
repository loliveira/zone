var Position = Backbone.Model.extend({
    fetch: function() {
      var that = this;
      GMaps.geolocate({
        success: function(position) {
          console.log("success");
          that.set({position: position});
        },
        error: function(error) {
          console.log('Geolocation failed: '+ error.message);
        },
        not_supported: function() {
          alert("Your browser does not support geolocation");
        },
        always: function() {
          console.log("geo done.");
        }
      });
    }});

  window.position = new Position;

  var PositionView = Backbone.View.extend({
    id: "position",

    initialize: function() {
      this.listenTo(this.model, "change", this.render);
    },

    render: function() {
      var p = this.model.get("position");

      $("#longitude").text(p.coords.longitude);
      $("#latitude").text(p.coords.latitude);
    }
  });

  //window.positionView = new PositionView({model: position});


  var Chat = Backbone.Model.extend({
    initialize: function(attributes, options) {
      var host = window.location.host;

      this.socket = new WebSocket("ws://" + host + "/ws");

      var that = this;

      that.socket.onmessage = function(event) {
        console.log("onmessage");
        console.log(event);

        that.set({msg: "open"});
      }
      that.socket.onopen = function(event) {
        console.log("open....");
        that.set({state: "open"});
      }
      that.socket.onerror = function(event) {
        console.log("onerror");
        console.log(event);
      }
      that.socket.onclose = function(event) {
        that.set({state: "close"});
      }
    }
  });

  window.chat = new Chat;

  var ChatView = Backbone.View.extend({
      id: "messages",

      initialize: function() {
        this.listenTo(this.model, "change", this.render);
        this.listenTo(this.model, "state", this.onnewstate);
      },

      onnewstate: function () {
        console.log("onnewstate");
        console.log(onnewstate);
      },

      render: function() {

        // console.log(arguments[0]);
        // console.log(arguments[1]);
      }
    });

  window.chatView = new ChatView({model: chat});















function reconnect (callback) {
  if (window.socket) {
    window.socket.close();
  }
  //window.socket = Chat(callback);
}

function sendChat () {
  var msg = $("#text").val();
  var nick = $("#nick").val();
  var data = JSON.stringify({msg: msg,
                             nick: nick});

  socket.send(data);
}

function sendPosition () {
  var data = JSON.stringify({coords: window.coords});

  socket.send(data);
}

function UpdatePosition (position) {
  window.coords = {};
  window.coords.latitude = position.coords.latitude;
  window.coords.longitude = position.coords.longitude;

  $("#longitude").text(window.coords.longitude);
  $("#latitude").text(window.coords.latitude);
}

function setOnline() {
  $("#status").text("online");
  sendPosition();
}

function setOffline() {
  $("#status").text("offline");
}


// $("#messages").append("<li>"+ event.data + "</li>");

