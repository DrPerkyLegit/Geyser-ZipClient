package org.geyser.extension.zipclient.GUI;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.geyser.extension.zipclient.ZipClientExtension;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.geyser.api.connection.GeyserConnection;

public class ZipClientForm {
    private static String name = "ZipClient";
    private SimpleForm internalForm;
    private final GeyserConnection connection;
    private final ZipClientExtension extension;

    public ZipClientForm(ZipClientExtension extension, @NonNull GeyserConnection connection) {
        this.connection = connection;
        this.extension = extension;
        this.updateMenu();

    }

    public SimpleForm createForm() {
        return SimpleForm.builder()
                .title(name)
                .content("Page Content")
                .button("Config")
                .button("Next Page", FormImage.of(FormImage.Type.PATH, ""))
                .button("Back Page")
                .closedResultHandler(() -> {
                    return;
                }).validResultHandler((response) -> {
                    //response.clickedButtonId()
                })
                .build();
    }

    private void updateMenu() {
        internalForm = createForm();
    }

    public void sendForm() {
        connection.sendForm(internalForm);
    }

}
