import React, {Component} from 'react'
import {Col, Row} from "reactstrap";
import axios from "axios";
import {Grid} from "react-bootstrap";
import FormInputs from "../components/FormInputs/FormInputs";
import CustomButton from "../components/CustomButton/CustomButton";
import Card from "../components/Card/Card";
import Loader from "react-loader-spinner";

/** /register
 * Register page*/
class Register extends Component {
    constructor(props) {
        super(props)
        this.state = {
            isRegistering: false
        }
        document.title = "MovieLand Register";
    }

    /** Get Data from Form and validate to send register request*/
    registerClicked(ev) {
        ev.preventDefault();
        const data = new FormData(ev.target);
        let pwd = data.get('password'), user = data.get('username'), name = data.get('name');
        if (pwd.length < 5) {
            this.props.showNotification("Password at least 5 characters","error","bc");
            return;
        }
        if (user.length < 4 || name.length < 4) {
            this.props.showNotification("Username and Full Name at least 4 characters","error","bc");
            return;
        }
        if (pwd !== data.get('repeat')) {
            console.log("Repaet not matching")
            this.props.showNotification("Passwords not matching! ","error","bc");
            return;
        }
        this.setState({isRegistering: true});
        axios.post(`/api/auth/signup`, {
                name: name, username: user, email: user + "@mail.com", password: pwd
            },
            {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            })
            .then(response => {
                this.setState({isRegistering: false});
                this.props.showNotification("Registration successful ","success","bc");
                console.log(response);
            })
            .catch(err => {
                this.setState({isRegistering: false});
                console.log(err.response.data.message);
                this.props.showNotification("Registration failed: "
                    + (err.response.data.message ? err.response.data.message : "Server is unreachable"), "error", "bc");
            });
    }

    render() {
        return (
            <div className="content">
                <Grid fluid>
                    <Row>
                        <Col md={8}>
                            <div className="whole-height">
                                <Card
                                    title="Register Account"
                                    content={
                                        <form onSubmit={this.registerClicked.bind(this)}>
                                            <FormInputs
                                                ncols={["col-md-8"]}
                                                properties={[{
                                                    label: "Full Name",
                                                    name: "name",
                                                    type: "text",
                                                    bsClass: "form-control",
                                                    placeholder: "Full Name"
                                                }]}
                                            />
                                            <FormInputs
                                                ncols={["col-md-8"]}
                                                properties={[{
                                                    label: "Username",
                                                    name: "username",
                                                    type: "text",
                                                    bsClass: "form-control",
                                                    placeholder: "Username"
                                                }]}
                                            />
                                            <FormInputs
                                                ncols={["col-md-8"]}
                                                properties={[{
                                                    label: "Password",
                                                    name: "password",
                                                    type: "password",
                                                    bsClass: "form-control",
                                                    placeholder: "Password"
                                                }]}
                                            />
                                            <FormInputs
                                                ncols={["col-md-8"]}
                                                properties={[{
                                                    label: "Repeat",
                                                    name: "repeat",
                                                    type: "password",
                                                    bsClass: "form-control",
                                                    placeholder: "Repeat"
                                                }]}
                                            />
                                            <CustomButton bsStyle="primary" pullLeft fill type="submit">
                                                {this.state.isRegistering && (<Loader
                                                    type="Oval"
                                                    color="#FFF"
                                                    height={16}
                                                    width={16}
                                                />) || "Register"}
                                            </CustomButton>
                                            <div className="clearfix"/>
                                        </form>}
                                />
                            </div>
                        </Col>
                    </Row>
                </Grid>
            </div>
        )
    }
}

export default Register