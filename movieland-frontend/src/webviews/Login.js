import React, {Component} from 'react'
import AuthService from "../service/AuthenticationService";
import history from '../history';
import AuthenticationService from "../service/AuthenticationService";
import {Alert, Col, Container, FormFeedback, FormGroup, Input, Label, Row} from "reactstrap";
import CustomButton from "../components/CustomButton/CustomButton";
import App from "../App";
import {Button} from "reactstrap";
import {Grid} from "react-bootstrap";
import Card from "../components/Card/Card";
import FormInputs from "../components/FormInputs/FormInputs";

/** /login page Component
 *  Logout Page for JWT Authentication*/
class Login extends Component {
    constructor(props) {
        super(props);
        let msg = null;
        if (this.props.location.state != null) {
            msg = this.props.location.state.msg;
        }
        this.state = {
            hasLoginFailed: false,
            showSuccessMessage: false,
            msg: msg
        }
        this.loginClicked = this.loginClicked.bind(this)

        document.title = "MovieLand Logout";
    }

    componentDidMount() {

    }


    /** Execute Jwt Authentication and forward to last page if successful*/
    loginClicked(ev) {
        ev.preventDefault();
        const data = new FormData(ev.target);
        let user=data.get('username');
        let pwd=data.get('password');
        AuthService
            .executeJwtAuthenticationService(user, pwd)
            .then((resp) => {
                console.log("Successfull login");
                console.log("Token: " + resp.data.accessToken);
                this.props.showNotification("Login successfull","success","bc");
                AuthenticationService.setUserName(user);
                AuthenticationService.registerJwtSuccessfulLogin(resp.data.accessToken);
                this.props.onAction("Login");
                this.setState({showSuccessMessage: true, hasLoginFailed: false});
                if (this.props.location.state == null || this.props.location.state.previous === "/login") {
                    console.log("to: /")
                    history.push("/");
                } else {
                    console.log("to: " + this.props.location.state.previous)
                    history.push(this.props.location.state.previous);
                }

            }).catch((err) => {
            console.log(err)
            this.props.showNotification("Login failed: "+err.response.data.message,"error","bc");
           /* this.setState({showSuccessMessage: false})
            this.setState({hasLoginFailed: true})*/
        })
    }

    render() {
        return (
            <div className="content whole-height">
                <Grid fluid>
                    <Row>
                        <Col md={8}>
                            <Card
                                title="Login"
                                content={
                                    <form onSubmit={this.loginClicked.bind(this)}>
                                        <FormInputs
                                            ncols={["col-md-8"]}
                                            properties={[{
                                                label: "Username",
                                                name: "username",
                                                type: "text",
                                                bsClass: "form-control",
                                                placeholder: "Username",
                                                defaultValue: "admin"
                                            }]}
                                        />
                                        <FormInputs
                                            ncols={["col-md-8"]}
                                            properties={[{
                                                label: "Password",
                                                name: "password",
                                                type: "password",
                                                bsClass: "form-control",
                                                placeholder: "Password",
                                                defaultValue: "admin123"
                                            }]}
                                        />
                                        <CustomButton bsStyle="primary" pullLeft type="submit">Login</CustomButton>
                                    </form>
                                }
                            />
                        </Col>
                    </Row>
                </Grid>
            </div>
        )
    }
}

export default Login