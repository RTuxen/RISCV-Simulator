package sample;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class Main extends Application {

        @Override
        public void start(Stage stage) throws Exception {

            //Calls the simulator class to use the register's values
            Simulator sim = new Simulator();
            final ObservableList<Register> data = FXCollections.observableArrayList(

                    //Creates 32 Registers with register number and it's value
                    new Register("  zero (x0)", Integer.toString(sim.reg[0])),
                    new Register("   ra   (x1)", Integer.toString(sim.reg[1])),
                    new Register("   sp   (x2)", Integer.toString(sim.reg[2])),
                    new Register("   gp  (x3)", Integer.toString(sim.reg[3])),
                    new Register("   tp   (x4)", Integer.toString(sim.reg[4])),
                    new Register("   t0   (x5)", Integer.toString(sim.reg[5])),
                    new Register("   t1   (x6)", Integer.toString(sim.reg[6])),
                    new Register("   t2   (x7)", Integer.toString(sim.reg[7])),
                    new Register("   s0   (x8)", Integer.toString(sim.reg[8])),
                    new Register("   s1   (x9)", Integer.toString(sim.reg[9])),
                    new Register("   a0   (x10)", Integer.toString(sim.reg[10])),
                    new Register("   a1   (x11)", Integer.toString(sim.reg[11])),
                    new Register("   a2   (x12)", Integer.toString(sim.reg[12])),
                    new Register("   a3   (x13)", Integer.toString(sim.reg[13])),
                    new Register("   a4   (x14)", Integer.toString(sim.reg[14])),
                    new Register("   a5   (x15)", Integer.toString(sim.reg[15])),
                    new Register("   a6   (x16)", Integer.toString(sim.reg[16])),
                    new Register("   a7   (x17)", Integer.toString(sim.reg[17])),
                    new Register("   s2   (x18)", Integer.toString(sim.reg[18])),
                    new Register("   s3   (x19)", Integer.toString(sim.reg[19])),
                    new Register("   s4   (x20)", Integer.toString(sim.reg[20])),
                    new Register("   s5   (x21)", Integer.toString(sim.reg[21])),
                    new Register("   s6   (x22)", Integer.toString(sim.reg[22])),
                    new Register("   s7   (x23)", Integer.toString(sim.reg[23])),
                    new Register("   s8   (x24)", Integer.toString(sim.reg[24])),
                    new Register("   s9   (x25)", Integer.toString(sim.reg[25])),
                    new Register("   s10 (x26)", Integer.toString(sim.reg[26])),
                    new Register("   s11 (x27)", Integer.toString(sim.reg[27])),
                    new Register("   t3   (x28)", Integer.toString(sim.reg[28])),
                    new Register("   t4   (x29)", Integer.toString(sim.reg[29])),
                    new Register("   t5   (x30)", Integer.toString(sim.reg[30])),
                    new Register("   t6   (x31)", Integer.toString(sim.reg[31]))
            );

            //Creates a scene
            Scene scene = new Scene(new Group());

            //Sets the title and size of the window
            stage.setTitle("RISC-V Simulator");
            stage.setWidth(340);
            stage.setHeight(1028);

            //Sets the table
            TableView<Register> table = new TableView<Register>();
            table.setMinHeight(960);

            //First column
            TableColumn regCol = new TableColumn("Registers");
            regCol.setMinWidth(150);
            regCol.setCellValueFactory(new PropertyValueFactory<Register, String>("register"));

            //Second Column
            TableColumn valCol = new TableColumn("Values");
            valCol.setMinWidth(150);
            valCol.setCellValueFactory(new PropertyValueFactory<Register, String>("value"));

            table.setItems(data);
            table.getColumns().addAll(regCol, valCol);

            //Sets up the VBox used to hold the table
            final VBox vbox = new VBox();
            vbox.setSpacing(5);
            vbox.setPadding(new Insets(10, 10, 10, 10));
            vbox.getChildren().addAll(table);

            ((Group) scene.getRoot()).getChildren().addAll(vbox);

            stage.setScene(scene);
            stage.show();
        }

        public static class Register {
            private final SimpleStringProperty register;
            private final SimpleStringProperty value;

            private Register(String register, String value) {
                this.register = new SimpleStringProperty(register);
                this.value = new SimpleStringProperty(value);
            }

            public void setRegister(String reg) {
                register.set(reg);
            }

            public String getRegister() {
                return register.get();
            }

            public void setValue(String val) {
                value.set(val);
            }

            public String getValue() {
                return value.get();
            }
        }
}