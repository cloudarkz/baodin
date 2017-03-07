<?php
require_once("database/MySQLController.php");

/** Ajax Request Controller
 * This file serves as an intermediary between client and server. All ajax request from client side
 * will come to this file before server is asked to perform the respective processes. Action is defined
 * in JSON.
 *
 * When the function is completed and tested, remove the to do label from the respective comments.
 */

$req = json_decode(file_get_contents('php://input'), true);

switch($req['cmd']){

    /** @Tutorial
     * @params
     * All parameters can be access directly from $req if it is defined in this section.
     * IE. $req['clientID'];
     *
     * @echo
     * All responses expect a JSON encoded associative array. Array should contain @response, @data, @extra.
     * To keep things simple @response, @data & @extra should be in string format
     *
     * @response is always {0 : Success}, {any other integer : Error Code}
     *
     * @data is the main return variable.
     * @extra is only used when an additional variable is required. This is usually used when you want to client side
     * to use a different variable when replying to server. See cmd = getQueuedLinks & cmd = setLinkDownloaded;
     *
     * @data & @extra will always run a search for reserved word #
     * if # is found, @data & @add will be split into arrays, only use # between 2 entries, IE. apple#orange#pear
     */

    case 'authenticate':
        /** This request authenticate login details with server database
         * @params
         *  email - VARCHAR(50)
         * @params
         *  password - VARCHAR(50)
         * @echo:data
         *  accountID - String, Auto generated 20 characters
         *  name - String
         *  email - String
         *  contactNumber - String
         */

        $result = $mysqli->authenticateLogin($req["email"], $req["password"]);

        echo json_encode(array(
            'response' => $result[0],
            'data' => $result[1]
        ));
        break;

    case 'get-account-details':
        /** This request will test run getting account details
         * @params
         *  accountID - VARCHAR(20)
         * @echo:data
         *  name - String
         *  email - String
         *  contactNumber - String
         */

        $result = $mysqli->getAccountDetails($req["accountID"]);

        echo json_encode(array(
            'response' => $result[0],
            'data' => $result[1]
        ));
        break;

    case 'search-accounts':
            /** This request will test run searching for accounts
             * @params
             *  searchParam - String
             * @echo:data
             *  accountID - String
             */

            $result = $mysqli->searchAccount($req["searchParam"]);

            echo json_encode(array(
                'response' => $result[0],
                'data' => $result[1]
            ));
            break;
}
