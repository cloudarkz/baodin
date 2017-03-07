<?php
if(file_exists("../../../resources/shout/config.php"))
    require_once("../../../resources/shout/config.php");
else if(file_exists("../../../../resources/shout/config.php"))
    require_once("../../../../resources/shout/config.php");
else
    require_once("config.php");

date_default_timezone_set("Singapore");
$mysqli = new MySQLController($sql_details);

class MySQLController {
    var $mysqli;

    function MySQLController ($sql_details){
        $this->mysqli = new mysqli($sql_details["host"],$sql_details["user"],$sql_details["pass"],$sql_details["db"]);

        if ($this->mysqli->connect_errno) {
            // Something you should not do on a public site, but this example will show you
            // anyways, is print out MySQL error related information -- you might log this
            echo "Error: Failed to make a MySQL connection, here is why: \n";
            echo "Error: " . $this->mysqli->connect_errno . "\n";
            echo "Error: " . $this->mysqli->connect_error . "\n";

            // You might want to show them something nice, but we will simply exit
            exit;
        }
    }

    function authenticateLogin( $email, $password ){
        $stmt = $this->mysqli->prepare("SELECT accountID FROM LoginAccounts WHERE email = ? AND BINARY password = ?");
        $stmt->bind_param('ss', $email, $password);
        $stmt->execute();
        $stmt->bind_result($accountID);
        $stmt->fetch();

        if($stmt->errno != 0){
            $error = $stmt->error;
            $stmt->close();

            return array(1, "Database error: ".$error);
        }

        $stmt->close();

        if (!empty($accountID)) {
            return array(0, $accountID);
        }
        else{
            return array(1, "Username and/or password is invalid.");
        }
    }

    function getAccountDetails( $accountID ){
        $stmt = $this->mysqli->prepare("SELECT name, email, contactNumber FROM LoginAccounts WHERE accountID = ?");
        $stmt->bind_param('s', $accountID);
        $stmt->execute();
        $stmt->bind_result($name, $email, $contactNumber);
        $stmt->fetch();

        if($stmt->errno != 0){
            $error = $stmt->error;
            $stmt->close();

            return array(1, "Database error: ".$error);
        }

        $stmt->close();

        if (!empty($name)) {
            return array(0, array(  "accountID" => $accountID,
                                    "name" => $name,
                                    "email" => $email,
                                    "contactNumber" => $contactNumber ));
        }
        else{
            return array(1, "Account ID is invalid.");
        }
    }

    function searchAccount( $searchParam ){
            $stmt = $this->mysqli->prepare("SELECT accountID FROM LoginAccounts WHERE CONCAT_WS(',', name, email, contactNumber) LIKE ?");
            $stmt->bind_param('s', '%'.$searchParam.'%');
            $stmt->execute();
            $stmt->bind_result($accountID);
            $stmt->fetch();

            if($stmt->errno != 0){
                $error = $stmt->error;
                $stmt->close();

                return array(1, "Database error: ".$error);
            }

            $stmt->close();

            if (!empty($name)) {
                return array(0, $accountID);
            }
            else{
                return array(1, "No result found.");
            }
        }
}
