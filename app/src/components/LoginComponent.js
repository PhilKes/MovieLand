import React, {Component} from 'react'
import AuthService from "./AuthenticationService";
import history from '../history';
import AuthenticationService from "./AuthenticationService";
import AppNavbar from "../AppNavbar";
import {Button, Container, FormFeedback, FormGroup, Input, Label} from "reactstrap";
import App from "../App";

class LoginComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            username: 'admin',
            password: 'admin123',
            hasLoginFailed: false,
            showSuccessMessage: false
        }

        this.handleChange = this.handleChange.bind(this)
        this.loginClicked = this.loginClicked.bind(this)
    }

    handleChange(event) {
        this.setState(
            {
                [event.target.name]: event.target.value
            }
        )
    }

    loginClicked() {
        /* if(this.state.username==='admin' && this.state.password==='admin'){
             /!*AuthService.registerSuccessfulLogin(this.state.username,this.state.password)*!/
             this.props.history.push(`/movies`);
             this.setState({showSuccessMessage:true})
             this.setState({hasLoginFailed:false})
         }
         else {
             this.setState({showSuccessMessage:false})
             this.setState({hasLoginFailed:true})
         }*/
        AuthService
            .executeJwtAuthenticationService(this.state.username, this.state.password)
            .then((resp) => {
                console.log("Successfull login");
                console.log("Token: " + resp.data.accessToken);
                /*AuthenticationService.setUserName(this.state.username);*/
                AuthenticationService.registerJwtSuccessfulLogin(resp.data.accessToken);
                // this.props.parent.setState({isLoggedIn: true, currUser: this.state.username});
                //TODO
                this.props.onLogin(true, this.state.username);
                //history.go(-1);
                //history.go(-2);
            }).catch(() => {
            this.setState({showSuccessMessage: false})
            this.setState({hasLoginFailed: true})
        })
    }

    render() {
        return (
            <div>
                <AppNavbar/>
                <Container fluid>
                    <h2>Login</h2>
                    <div className="container">
                        {this.state.hasLoginFailed && <div className="alert alert-warning">Invalid Credentials</div>}
                        {this.state.showSuccessMessage && <div>Login Sucessful</div>}
                        <FormGroup>
                            <Label>Username</Label>
                            <Input type="text" name="username"
                                   className="col-md-4"
                                   value={this.state.username}
                                   onChange={this.handleChange}/>
                            <Label>Password</Label>
                            <Input type="password" name="password"
                                   className="col-md-4"
                                   value={this.state.password}
                                   onChange={this.handleChange}/><br/>
                            {/* <FormFeedback invalid>Invalid Credentials!</FormFeedback>*/}
                            <Button className="btn btn-success" onClick={this.loginClicked}>Login</Button>
                        </FormGroup>
                    </div>
                </Container>
            </div>
        )
    }
}

export default LoginComponent