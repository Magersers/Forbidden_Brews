package io.github.magersers.forbiddenbrews.client;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
public final class BruteModelLayer {
    private BruteModelLayer() {}
    public static LayerDefinition create() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-10.000F, -19.000F, -5.000F, 20.000F, 19.000F, 10.000F)
            .texOffs(0, 30).addBox(-10.200F, -1.000F, -5.200F, 20.400F, 2.000F, 10.400F)
            .texOffs(65, 30).addBox(-1.500F, -1.000F, -5.500F, 3.000F, 2.000F, 0.300F), PartPose.offset(0.000F, 9.000F, 0.000F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(61, 0).addBox(-4.000F, -2.000F, -4.000F, 8.000F, 4.000F, 8.000F)
            .texOffs(94, 0).addBox(-6.000F, -12.000F, -6.000F, 12.000F, 12.000F, 12.000F)
            .texOffs(143, 0).addBox(-6.200F, -12.200F, -6.200F, 12.400F, 3.200F, 12.400F)
            .texOffs(196, 0).addBox(-6.100F, -9.000F, 5.900F, 12.200F, 5.000F, 0.400F)
            .texOffs(225, 0).addBox(-5.000F, -7.500F, -6.400F, 3.500F, 0.800F, 0.500F)
            .texOffs(236, 0).addBox(1.500F, -7.500F, -6.400F, 3.500F, 0.800F, 0.500F), PartPose.offset(0.000F, -19.000F, 0.000F));
        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create()
            .texOffs(74, 30).addBox(-5.500F, -2.000F, -4.500F, 9.000F, 13.000F, 9.000F)
            .texOffs(111, 30).addBox(-5.700F, -2.200F, -4.700F, 9.400F, 5.200F, 9.400F)
            .texOffs(152, 30).addBox(-6.000F, 9.000F, -5.000F, 10.000F, 13.000F, 10.000F)
            .texOffs(193, 30).addBox(-7.000F, 16.000F, -7.000F, 12.000F, 10.000F, 12.000F)
            .texOffs(0, 54).addBox(-6.500F, 9.000F, -5.500F, 11.000F, 7.000F, 11.000F)
            .texOffs(45, 54).addBox(-6.500F, -4.000F, -5.500F, 11.000F, 4.000F, 11.000F)
            .texOffs(90, 54).addBox(-7.500F, -7.000F, -1.500F, 3.000F, 4.000F, 3.000F)
            .texOffs(103, 54).addBox(-5.500F, 19.000F, -8.000F, 2.500F, 3.000F, 1.200F)
            .texOffs(114, 54).addBox(-2.500F, 19.000F, -8.000F, 2.500F, 3.000F, 1.200F)
            .texOffs(125, 54).addBox(0.500F, 19.000F, -8.000F, 2.500F, 3.000F, 1.200F), PartPose.offset(-12.000F, -16.000F, 0.000F));
        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create()
            .texOffs(0, 78).addBox(-3.500F, -2.000F, -4.500F, 9.000F, 13.000F, 9.000F)
            .texOffs(37, 78).addBox(-3.700F, -2.200F, -4.700F, 9.400F, 5.200F, 9.400F)
            .texOffs(78, 78).addBox(-4.000F, 9.000F, -5.000F, 10.000F, 13.000F, 10.000F)
            .texOffs(119, 78).addBox(-5.000F, 16.000F, -7.000F, 12.000F, 10.000F, 12.000F)
            .texOffs(168, 78).addBox(-4.500F, 9.000F, -5.500F, 11.000F, 7.000F, 11.000F)
            .texOffs(0, 102).addBox(-4.500F, -4.000F, -5.500F, 11.000F, 4.000F, 11.000F)
            .texOffs(45, 102).addBox(4.500F, -7.000F, -1.500F, 3.000F, 4.000F, 3.000F)
            .texOffs(58, 102).addBox(-3.500F, 19.000F, -8.000F, 2.500F, 3.000F, 1.200F)
            .texOffs(69, 102).addBox(-0.500F, 19.000F, -8.000F, 2.500F, 3.000F, 1.200F)
            .texOffs(80, 102).addBox(2.500F, 19.000F, -8.000F, 2.500F, 3.000F, 1.200F), PartPose.offset(12.000F, -16.000F, 0.000F));
        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create()
            .texOffs(136, 54).addBox(-4.500F, -1.000F, -4.500F, 9.000F, 14.000F, 9.000F)
            .texOffs(173, 54).addBox(-4.700F, 10.000F, -5.500F, 9.400F, 4.000F, 10.200F)
            .texOffs(216, 54).addBox(-3.000F, 4.000F, -5.000F, 6.000F, 4.000F, 0.700F), PartPose.offset(-5.000F, 10.000F, 0.000F));
        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create()
            .texOffs(91, 102).addBox(-4.500F, -1.000F, -4.500F, 9.000F, 14.000F, 9.000F)
            .texOffs(128, 102).addBox(-4.700F, 10.000F, -5.500F, 9.400F, 4.000F, 10.200F)
            .texOffs(171, 102).addBox(-3.000F, 4.000F, -5.000F, 6.000F, 4.000F, 0.700F), PartPose.offset(5.000F, 10.000F, 0.000F));
        return LayerDefinition.create(mesh, 256, 256);
    }
}
