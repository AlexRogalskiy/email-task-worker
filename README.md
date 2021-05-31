# email-task-worker
A CAMUNDA external task worker which sends email

# Setup
Deploy the process model from [/model/sendEmail.bpmn](/model/sendEmail.bpmn)  

Adjust credentials in [/src/main/resources/application.yml](/src/main/resources/application.yml)  
   
If 2-factor-authentication is enabled for your account, you generate an
application secret as described in: [Sign in with App Passwords](https://support.google.com/accounts/answer/185833)

If you sue your own process model, ensure the subscription configuration matches the ids used in your model.