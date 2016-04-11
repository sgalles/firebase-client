```
{
    "rules": {
      "motioneyeos":{
        ".read": "auth !== null && root.child('users').hasChild(auth.uid)",
        ".write": "auth !== null && root.child('users').hasChild(auth.uid)"
      },
      "firewall":{
        ".read": "auth !== null && root.child('users').hasChild(auth.uid)",
        ".write": "auth !== null && root.child('users').hasChild(auth.uid)"
      },
      "model":{
        ".read": "auth !== null && root.child('users').hasChild(auth.uid)",
        ".write": "auth !== null && root.child('users').hasChild(auth.uid)"
      }
    }
}
``` 