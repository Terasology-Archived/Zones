/*
 * Copyright 2012 Benjamin Glatzel <benjamin.glatzel@me.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.zone.nui;

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector2i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureUtil;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.itemRendering.StringTextRenderer;
import org.terasology.rendering.nui.layouts.ColumnLayout;
import org.terasology.rendering.nui.layouts.RowLayoutHint;
import org.terasology.rendering.nui.layouts.relative.HorizontalHint;
import org.terasology.rendering.nui.layouts.relative.RelativeLayout;
import org.terasology.rendering.nui.layouts.relative.RelativeLayoutHint;
import org.terasology.rendering.nui.layouts.relative.VerticalHint;
import org.terasology.rendering.nui.widgets.ActivateEventListener;
import org.terasology.rendering.nui.widgets.ItemActivateEventListener;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UIDropdown;
import org.terasology.rendering.nui.widgets.UIImage;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIList;
import org.terasology.rendering.nui.widgets.UISpace;
import org.terasology.rendering.nui.widgets.UIText;
import org.terasology.utilities.Assets;
import org.terasology.world.selection.BlockSelectionComponent;
import org.terasology.zone.Constants;
import org.terasology.zone.ZoneComponent;
import org.terasology.zone.ZoneTrackingSystem;
import org.terasology.zone.ZoneType;
import org.terasology.zone.selection.ZoneToolSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UIZoneBook extends CoreScreenLayer {
    private static final int MAX_SELECTED_BOUNDS = 50;

    /*
     * @In private LocalPlayer localPlayer;
     *
     * @In private EntityManager entityManager;
     */
    private final UIImage background;
    private final UILabel lblzonename, lblheight, lbldepth, lblwidth, lblzonetype, lblError;
    private final UIText txtzonename, txtheight, txtdepth, txtwidth;
    private final UIDropdown<ZoneType> cmbType;
    private UIList<EntityRef> uizonelist;
    private UIList<ZoneType> uizonelistgroup;
    private UIButton btnSave, btnDelete, btnBack;

    private EntityRef lastSelectedZone = EntityRef.NULL;

    private StringTextRenderer<ZoneType> zoneTypeRenderer = new StringTextRenderer<ZoneType>() {
        @Override
        public String getString(ZoneType zoneType) {
            return zoneType.getDescription();
        }
    };

    private StringTextRenderer<EntityRef> zoneItemRenderer = new StringTextRenderer<EntityRef>() {
        @Override
        public String getString(EntityRef zone) {
            ZoneComponent zoneComponent = zone.getComponent(ZoneComponent.class);
            String zoneName = zoneComponent.Name;
            return zoneName;
        }
    };

    private ItemActivateEventListener<ZoneType> zoneTypeSelectionListener = new ItemActivateEventListener<ZoneType>() {
        @Override
        public void onItemActivated(UIWidget widget, ZoneType zoneType) {
            List<EntityRef> currentZoneList = null;
            switch (zoneType) {
                case Gather: {
                    currentZoneList = ZoneTrackingSystem.getGatherZoneList();
                    break;
                }
                case Terraform: {
                    currentZoneList = ZoneTrackingSystem.getTerraformZoneList();
                    break;
                }
                case Work: {
                    currentZoneList = ZoneTrackingSystem.getWorkZoneList();
                    break;
                }
                case Storage: {
                    currentZoneList = ZoneTrackingSystem.getStorageZoneList();
                    break;
                }
                case OreonFarm: {
                    currentZoneList = ZoneTrackingSystem.getOreonFarmZoneList();
                    break;
                }
                default: {
                    break;
                }
            }

            if (null != currentZoneList) {
                uizonelist.setList(currentZoneList);
                uizonelist.setVisible(true);
                uizonelistgroup.setVisible(false);
                btnBack.setVisible(true);
            }
        }
    };

    private ItemActivateEventListener<EntityRef> zoneSelectionlistener = new ItemActivateEventListener<EntityRef>() {

        @Override
        public void onItemActivated(UIWidget widget, EntityRef item) {
//            if (cmbType.isVisible()) {
//                cmbType.setVisible(false);
//            }
            lblError.setText("");
            hideSelectedZone();
            EntityRef zone = item;
            ZoneComponent zoneComponent = zone.getComponent(ZoneComponent.class);
            txtzonename.setText(zoneComponent.Name);
            txtheight.setText("" + zoneComponent.getZoneHeight());
            txtwidth.setText("" + zoneComponent.getZoneWidth());
            txtdepth.setText("" + zoneComponent.getZoneDepth());
            switch (zoneComponent.zonetype) {
                case Gather: {
                    lblzonetype.setText("Zonetype : Gather");
                    break;
                }
                case Terraform: {
                    lblzonetype.setText("Zonetype : Terraform");
                    break;
                }
                case Work: {
                    lblzonetype.setText("Zonetype : Work");
                    break;
                }
                default: {
                    lblzonetype.setText("label wasn't set");
                    break;
                }
            }

            btnSave.setVisible(false);
            btnDelete.setVisible(true);
            lastSelectedZone = zone;
            showSelectedZone();
        }
    };

    public UIZoneBook() {

        super("zonebook");
        // is everything modal by default now?
        // setModal(true);

        //        maximize();
        //        setCloseKeys(new int[]{Keyboard.KEY_ESCAPE});


        RelativeLayout windowLayout = new RelativeLayout();
        this.setContents(windowLayout);

        background = new UIImage();
        background.setImage(Assets.getTexture(Constants.MODULE_NAME + ":" + "openbook").get());
        background.setVisible(true);
//        "layoutInfo" : {
//            "width" : 512,
//            "height" : 128,
//            "position-horizontal-center" : {},
//            "position-top" : {
//                "target" : "TOP",
//                "offset" : 48
//            }
//        }

        //        background.setHorizontalAlign(HorizontalAlign.CENTER);
        //        background.setVerticalAlign(EVerticalAlign.CENTER);
        RelativeLayoutHint backgroundRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(),
                VerticalHint.create().center());
        backgroundRelativeLayoutHint.setWidth(500);
        backgroundRelativeLayoutHint.setHeight(300);
        windowLayout.addWidget(background, backgroundRelativeLayoutHint);

        ColumnLayout bookLeftRightPageLayout = new ColumnLayout();
        bookLeftRightPageLayout.setColumns(2);
        // Match background for rest of layout area
        windowLayout.addWidget(bookLeftRightPageLayout, backgroundRelativeLayoutHint);

        RowLayoutHint rowLayoutHint = new RowLayoutHint();
        rowLayoutHint.setUseContentWidth(true);

        RelativeLayout bookLeftPageLayout = new RelativeLayout();
        bookLeftRightPageLayout.addWidget(bookLeftPageLayout);

        ColumnLayout bookRightPageLayout = new ColumnLayout();
        bookRightPageLayout.setColumns(2);
        bookLeftRightPageLayout.addWidget(bookRightPageLayout);

        uizonelist = new UIList<EntityRef>();
        uizonelist.setVisible(true);
        uizonelist.subscribe(zoneSelectionlistener);
        uizonelist.setItemRenderer(zoneItemRenderer);
//        uizonelist.setPosition(new Vector2f(40, 20));
        RelativeLayoutHint uizonelistRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 40
                VerticalHint.create().alignBottom()); // TODO: 20
        uizonelistRelativeLayoutHint.setWidth(200);
        uizonelistRelativeLayoutHint.setHeight(220);
        bookLeftPageLayout.addWidget(uizonelist, uizonelistRelativeLayoutHint);

        uizonelistgroup = new UIList<ZoneType>();
        uizonelistgroup.setVisible(true);
        uizonelistgroup.subscribe(zoneTypeSelectionListener);
        uizonelistgroup.setItemRenderer(zoneTypeRenderer);
        //        uizonelistgroup.setPosition(new Vector2f(40, 20));
        RelativeLayoutHint uizonelistgroupRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 40
                VerticalHint.create().alignTop()); // TODO: 20
        uizonelistgroupRelativeLayoutHint.setWidth(200);
        uizonelistgroupRelativeLayoutHint.setHeight(250);
        bookLeftPageLayout.addWidget(uizonelistgroup, uizonelistgroupRelativeLayoutHint);

        lblzonename = new UILabel("Zone name :");
        //        lblzonename.setColor(Color.toColorString(Color.BLACK));
        lblzonename.setVisible(true);
        //        lblzonename.setPosition(new Vector2f(260, 20));
        RelativeLayoutHint lblzonenameRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 260
                VerticalHint.create().center()); // TODO: 20
        lblzonenameRelativeLayoutHint.setUsingContentWidth(true);
        lblzonenameRelativeLayoutHint.setUsingContentHeight(true);
        bookRightPageLayout.addWidget(lblzonename, lblzonenameRelativeLayoutHint);

        txtzonename = new UIText();
        //        txtzonename.setColor(Color.toColorString(Color.BLACK));
        txtzonename.setVisible(true);
        //        txtzonename.setPosition(new Vector2f(350, 20));
        RelativeLayoutHint txtzonenameRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 350
                VerticalHint.create().center()); // TODO: 20
        txtzonenameRelativeLayoutHint.setWidth(80);
        txtzonenameRelativeLayoutHint.setHeight(20);
        bookRightPageLayout.addWidget(txtzonename, txtzonenameRelativeLayoutHint);

        lblheight = new UILabel("Height :");
        //        lblheight.setColor(Color.toColorString(Color.BLACK));
        lblheight.setVisible(true);
        //        lblheight.setPosition(new Vector2f(260, 40));
        RelativeLayoutHint lblheightRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 260
                VerticalHint.create().center()); // TODO: 40
        lblheightRelativeLayoutHint.setUsingContentWidth(true);
        lblheightRelativeLayoutHint.setUsingContentHeight(true);
        bookRightPageLayout.addWidget(lblheight, lblheightRelativeLayoutHint);

        txtheight = new UIText();
        //        txtheight.setColor(Color.toColorString(Color.BLACK));
        txtheight.setVisible(true);
        //        txtheight.setPosition(new Vector2f(350, 40));
        RelativeLayoutHint txtheightRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 350
                VerticalHint.create().center()); // TODO: 40
        txtheightRelativeLayoutHint.setWidth(20);
        txtheightRelativeLayoutHint.setHeight(10);
        bookRightPageLayout.addWidget(txtheight, txtheightRelativeLayoutHint);

        lblwidth = new UILabel("Width :");
        //        lblwidth.setColor(Color.toColorString(Color.BLACK));
        lblwidth.setVisible(true);
        //        lblwidth.setPosition(new Vector2f(260, 60));
        RelativeLayoutHint lblwidthRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 260
                VerticalHint.create().center()); // TODO: 60
        lblwidthRelativeLayoutHint.setUsingContentWidth(true);
        lblwidthRelativeLayoutHint.setUsingContentHeight(true);
        bookRightPageLayout.addWidget(lblwidth, lblwidthRelativeLayoutHint);

        txtwidth = new UIText();
        //        txtwidth.setColor(Color.toColorString(Color.BLACK));
        txtwidth.setVisible(true);
        //        txtwidth.setPosition(new Vector2f(350, 60));
        RelativeLayoutHint txtwidthRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 350
                VerticalHint.create().center()); // TODO: 60
        txtwidthRelativeLayoutHint.setWidth(30);
        txtwidthRelativeLayoutHint.setHeight(15);
        bookRightPageLayout.addWidget(txtwidth, txtwidthRelativeLayoutHint);

        lbldepth = new UILabel("Depth :");
        //        lbldepth.setColor(Color.toColorString(Color.BLACK));
        lbldepth.setVisible(true);
        //        lbldepth.setPosition(new Vector2f(260, 80));
        RelativeLayoutHint lbldepthRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 260
                VerticalHint.create().center()); // TODO: 80
        lbldepthRelativeLayoutHint.setUsingContentWidth(true);
        lbldepthRelativeLayoutHint.setUsingContentHeight(true);
        bookRightPageLayout.addWidget(lbldepth, lbldepthRelativeLayoutHint);

        txtdepth = new UIText();
        //        txtdepth.setColor(Color.toColorString(Color.BLACK));
        txtdepth.setVisible(true);
        //        txtdepth.setPosition(new Vector2f(350, 80));
        RelativeLayoutHint txtdepthRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 350
                VerticalHint.create().center()); // TODO: 80
        txtdepthRelativeLayoutHint.setWidth(80);
        txtdepthRelativeLayoutHint.setHeight(20);
        bookRightPageLayout.addWidget(txtdepth, txtdepthRelativeLayoutHint);

        lblzonetype = new UILabel("");
        //        lblzonetype.setColor(Color.toColorString(Color.BLACK));
        lblzonetype.setVisible(true);
        //        lblzonetype.setPosition(new Vector2f(260, 100));
        RelativeLayoutHint lblzonetypeRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 260
                VerticalHint.create().center()); // TODO: 100
        lblzonetypeRelativeLayoutHint.setUsingContentWidth(true);
        lblzonetypeRelativeLayoutHint.setUsingContentHeight(true);
        bookRightPageLayout.addWidget(lblzonetype, lblzonetypeRelativeLayoutHint);

        cmbType = new UIDropdown<ZoneType>();
        cmbType.setVisible(false);
        cmbType.setOptionRenderer(zoneTypeRenderer);
        // cmbType = new UIDropdown<ZoneType>(new Vector2f(80, 20), new Vector2f(80, 200));
        // cmbType.setPosition(new Vector2f(350, 100));
        RelativeLayoutHint cmbTypeRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 350
                VerticalHint.create().center()); // TODO: 100
        cmbTypeRelativeLayoutHint.setWidth(80);
        cmbTypeRelativeLayoutHint.setHeight(20);
        bookRightPageLayout.addWidget(cmbType, cmbTypeRelativeLayoutHint);
        initTypes();

        lblError = new UILabel("");
        //        lblError.setWrap(true);
        //        lblError.setColor(Color.toColorString(Color.RED));
        lblError.setVisible(true);
        //        lblError.setPosition(new Vector2f(260, 130));
        RelativeLayoutHint lblErrorRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 260
                VerticalHint.create().center()); // TODO: 130
        lblErrorRelativeLayoutHint.setWidth(200);
        lblErrorRelativeLayoutHint.setHeight(80);
        bookRightPageLayout.addWidget(lblError, lblErrorRelativeLayoutHint);

        btnSave = new UIButton();
        btnSave.setText("Save");
        btnSave.setVisible(true);
        btnSave.subscribe(new ActivateEventListener() {
            @Override
            public void onActivated(UIWidget widget) {
                saveZone();
            }
        });
        //        btnSave.setPosition(new Vector2f(260, 230));
        RelativeLayoutHint btnSaveRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 260
                VerticalHint.create().center()); // TODO: 230
        btnSaveRelativeLayoutHint.setWidth(50);
        btnSaveRelativeLayoutHint.setHeight(20);
        bookRightPageLayout.addWidget(btnSave, btnSaveRelativeLayoutHint);

        btnDelete = new UIButton();
        btnDelete.setText("Delete");
        //        btnDelete.setId("btnDelZone");
        btnDelete.setVisible(false);
        btnDelete.subscribe(new ActivateEventListener() {
            @Override
            public void onActivated(UIWidget widget) {
                EntityRef zone = uizonelist.getSelection();
                deleteZone(zone);
            }
        });
        //        btnDelete.setPosition(new Vector2f(260, 230));
        RelativeLayoutHint btnDeleteRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 260
                VerticalHint.create().center()); // TODO: 230
        btnDeleteRelativeLayoutHint.setWidth(50);
        btnDeleteRelativeLayoutHint.setHeight(20);
        bookRightPageLayout.addWidget(btnDelete, btnDeleteRelativeLayoutHint);

        btnBack = new UIButton();
        btnBack.setText("Back");
        //        btnBack.setId("btnBack");
        btnBack.setVisible(false);
        btnBack.subscribe(new ActivateEventListener() {
            @Override
            public void onActivated(UIWidget widget) {
                initList();
                btnBack.setVisible(false);
            }
        });
        //        btnBack.setPosition(new Vector2f(40, 240));
        RelativeLayoutHint btnBackRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 40
                VerticalHint.create().center()); // TODO: 240
        btnBackRelativeLayoutHint.setWidth(50);
        btnBackRelativeLayoutHint.setHeight(20);
        bookRightPageLayout.addWidget(btnBack, btnBackRelativeLayoutHint);

        UIButton btnClose = new UIButton();
        btnClose.setText("Close");
        btnClose.setVisible(true);
        btnClose.subscribe(new ActivateEventListener() {
            @Override
            public void onActivated(UIWidget widget) {
                close();
            }
        });
        RelativeLayoutHint btnCloseRelativeLayoutHint = new RelativeLayoutHint(
                HorizontalHint.create().center(), // TODO: 40
                VerticalHint.create().center()); // TODO: 240
        btnCloseRelativeLayoutHint.setWidth(50);
        btnCloseRelativeLayoutHint.setHeight(20);
        bookRightPageLayout.addWidget(btnClose, btnCloseRelativeLayoutHint);


        UISpace fillUpSpace = new UISpace(new Vector2i(1, 60));
        bookRightPageLayout.addWidget(fillUpSpace);
    }

    private void saveZone() {
        lblError.setText("");
        ZoneToolSystem zoneToolSystem = CoreRegistry.get(ZoneToolSystem.class);
        if (null == zoneToolSystem.getCurrentlySelectedRegion()) {
            lblError.setText("Something went wrong. Please close the book and recreate the selection.");
        }
        if ((!cmbType.isVisible())) {
            this.close();
        }
        if (cmbType.isVisible() && cmbType.getSelection() == null) {
            lblError.setText("Please select a zone type");
            return;
        }
        if (cmbType.isVisible() && cmbType.getSelection() != null && (null == cmbType.getSelection())) {
            lblError.setText("Please select a zone type");
            return;
        }
        if (cmbType.isVisible() && cmbType.getSelection() != null) {
            ZoneType zoneType = cmbType.getSelection();
            if (zoneType == ZoneType.OreonFarm) {
                Region3i region = zoneToolSystem.getCurrentlySelectedRegion();
                if (region.min().y != region.max().y) {
                    lblError.setText("A farm zone needs to be level. Please select a flat zone and try again");
                    return;
                }
            }
        }
        String newZoneName = txtzonename.getText().trim();
        if (newZoneName.length() < 0) {
            lblError.setText("Zone name must be specified");
            return;
        }

        EntityManager entityManager = CoreRegistry.get(EntityManager.class);
        for (EntityRef zone : entityManager.getEntitiesWith(ZoneComponent.class)) {
            ZoneComponent zoneComponent = zone.getComponent(ZoneComponent.class);
            if (newZoneName.equalsIgnoreCase(zoneComponent.Name)) {
                lblError.setText("Zone name already exists!");
                return;
            }
        }

        int zoneheight;
        try {
            zoneheight = Integer.parseInt(txtheight.getText().trim());
        } catch (NumberFormatException e1) {
            lblError.setText("zone height needs to be an number");
            return;
        }
        int zonewidth;
        try {
            zonewidth = Integer.parseInt(txtwidth.getText());
        } catch (NumberFormatException e1) {
            lblError.setText("zone width needs to be an number");
            return;
        }
        int zonedepth;
        try {
            zonedepth = Integer.parseInt(txtdepth.getText());
        } catch (NumberFormatException e1) {
            lblError.setText("zone depth needs to be an number");
            return;
        }

        Region3i newZoneRegion = zoneToolSystem.getCurrentlySelectedRegion();
        Vector3i min = newZoneRegion.min();
        Vector3i newSize = new Vector3i(zonedepth, zoneheight, zonewidth);
        newZoneRegion = Region3i.createFromMinAndSize(min, newSize);

        ZoneComponent zoneComponent = new ZoneComponent(newZoneRegion);

        BlockSelectionComponent blockSelectionComponent = new BlockSelectionComponent();
        blockSelectionComponent.currentSelection = newZoneRegion;
        blockSelectionComponent.shouldRender = false;

        zoneComponent.Name = newZoneName;
        zoneComponent.zonetype = cmbType.getSelection();

        EntityRef newzone = entityManager.create(zoneComponent, blockSelectionComponent);

        newzone.saveComponent(zoneComponent);
        newzone.saveComponent(blockSelectionComponent);

        ZoneTrackingSystem.addZone(newzone);
        lblzonetype.setText("");
        zoneToolSystem.setCurrentlySelectedRegion(null);
        lastSelectedZone = newzone;
        showSelectedZone();
        close();
    }

    private void deleteZone(EntityRef deletezone) {
        ZoneComponent zoneComponent = deletezone.getComponent(ZoneComponent.class);
        switch (zoneComponent.zonetype) {
            case Gather: {
                hideSelectedZone(deletezone);
                ZoneTrackingSystem.getGatherZoneList().remove(deletezone);
                break;
            }
            case Work: {
                hideSelectedZone(deletezone);
                ZoneTrackingSystem.getWorkZoneList().remove(deletezone);
                break;
            }
            case Terraform: {
                hideSelectedZone(deletezone);
                ZoneTrackingSystem.getTerraformZoneList().remove(deletezone);
                break;
            }
            case Storage: {
                hideSelectedZone(deletezone);
                ZoneTrackingSystem.getStorageZoneList().remove(deletezone);
                break;
            }
            case OreonFarm: {
                hideSelectedZone(deletezone);
                ZoneTrackingSystem.getOreonFarmZoneList().remove(deletezone);
                break;
            }
        }
        fillUI();
    }

    @Override
    public void initialise() {
        open();
    }

    public void open() {
        fillUI();
    }

    public boolean outofboundselection(Region3i region) {
        boolean retval = false;
        if (getAbsoluteDiff(region.min().x, region.max().x) > MAX_SELECTED_BOUNDS) {
            retval = true;
        }
        if (getAbsoluteDiff(region.min().y, region.max().y) > MAX_SELECTED_BOUNDS) {
            retval = true;
        }
        if (getAbsoluteDiff(region.min().z, region.max().z) > MAX_SELECTED_BOUNDS) {
            retval = true;
        }
        return retval;
    }

    private int getAbsoluteDiff(int val1, int val2) {
        int width;
        if (val1 == val2) {
            width = 1;
        } else if (val1 < 0) {
            if (val2 < 0 && val2 < val1) {
                width = Math.abs(val2) - Math.abs(val1);
            } else if (val2 < 0 && val2 > val1) {
                width = Math.abs(val1) - Math.abs(val2);
            } else {
                width = Math.abs(val1) + val2;
            }
            width++;
        } else {
            if (val2 > -1 && val2 < val1) {
                width = val1 - val2;
            } else if (val2 > -1 && val2 > val1) {
                width = val2 - val1;
            } else {
                width = Math.abs(val2) + val1;
            }
            width++;
        }
        return width;
    }

    private void fillUI() {
        initList();
        resetInput();

        ZoneToolSystem zoneToolSystem = CoreRegistry.get(ZoneToolSystem.class);
        Region3i currentSelectedRegion = zoneToolSystem.getCurrentlySelectedRegion();
        if (null != currentSelectedRegion) {
            EntityManager entityManager = CoreRegistry.get(EntityManager.class);
            // TODO: this should really be a count of active zones owned by this player
            int zoneCount = entityManager.getCountOfEntitiesWith(ZoneComponent.class);

            txtzonename.setText("Zone" + String.valueOf(zoneCount));
            lblzonetype.setText("ZoneType :");
            cmbType.setVisible(true);
            txtwidth.setText(String.valueOf(ZoneComponent.getZoneWidth(currentSelectedRegion)));
            txtdepth.setText(String.valueOf(ZoneComponent.getZoneDepth(currentSelectedRegion)));
            txtheight.setText(String.valueOf(ZoneComponent.getZoneHeight(currentSelectedRegion)));

            if (outofboundselection(currentSelectedRegion)) {
                btnSave.setVisible(true);
                lblError.setText("The zone is to big to be saved, depth, width, height should not exceed 50");
            } else {
                btnSave.setVisible(true);
            }
            btnDelete.setVisible(false);
        }
    }

    private void initList() {
        //clear and init the list
        uizonelistgroup.setVisible(true);
        uizonelist.setVisible(false);
        uizonelistgroup.setList(Arrays.asList(ZoneType.values()));
    }

    private void initTypes() {
        List<ZoneType> optionsList = new ArrayList<ZoneType>();
        optionsList.add(ZoneType.Gather);
        optionsList.add(ZoneType.Terraform);
        optionsList.add(ZoneType.OreonFarm);
        cmbType.setOptions(optionsList);
    }

    private void resetInput() {
        //clear the textbowes
        txtzonename.setText("");
        txtheight.setText("");
        txtwidth.setText("");
        txtdepth.setText("");
        lblzonetype.setText("");
        lblError.setText("");
        btnSave.setVisible(false);
        btnDelete.setVisible(false);
    }

    private void hideSelectedZone() {
        hideSelectedZone(lastSelectedZone);
    }

    private void hideSelectedZone(EntityRef zone) {
        if (EntityRef.NULL != zone) {
            BlockSelectionComponent blockSelectionComponent = zone.getComponent(BlockSelectionComponent.class);
            blockSelectionComponent.shouldRender = false;
            zone.saveComponent(blockSelectionComponent);
        }
    }

    private void showSelectedZone() {
        if (EntityRef.NULL != lastSelectedZone) {
            BlockSelectionComponent blockSelectionComponent = lastSelectedZone.getComponent(BlockSelectionComponent.class);
            blockSelectionComponent.texture = Assets.get(TextureUtil.getTextureUriForColor(new Color(255, 255, 0, 100)), Texture.class).get();
            blockSelectionComponent.shouldRender = true;
            // we probably don't want to save a selected zone rendering state as on
            // zoneComponent.blockSelectionEntity.saveComponent(blockSelectionComponent);
        }
    }

    @Override
    public boolean isLowerLayerVisible() {
        return true;
    }

    public void shutdown() {
        hideSelectedZone();
        lastSelectedZone = EntityRef.NULL;
    }

    private void close() {
        shutdown();
        getManager().popScreen();
    }
}
